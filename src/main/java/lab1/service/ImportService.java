package lab1.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lab1.dao.ImportHistoryDAO;
import lab1.dao.LabWorkDAO;
import lab1.model.ImportHistory;
import lab1.model.LabWork;
import lab1.model.Coordinates;
import lab1.model.Discipline;
import lab1.model.Person;
import lab1.model.Location;
import lab1.rest.dtos.LabWorkDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Stateless
public class ImportService {
    
    private static final Logger logger = Logger.getLogger(ImportService.class.getName());
    
    private static final ConcurrentHashMap<String, ImportContext> activeImports = new ConcurrentHashMap<>();
    private static final int MAX_CONCURRENT_IMPORTS = 2;
    
    
    @Inject
    private LabWorkDAO labWorkDAO;
    
    @Inject
    private ImportHistoryDAO importHistoryDAO;
    
    private static class ImportContext {
        ImportHistory importHistory;
        long startTime;
        int totalItems;
        AtomicInteger processedItems;
        boolean completed;
        volatile int workerCount;
        ThreadPoolExecutor executorService;
        ScheduledExecutorService scalingScheduler;
        AtomicBoolean scalingInProgress;
        final Object scalingLock = new Object();
        volatile boolean scalingCompleted = false;
        List<CompletableFuture<ProcessingResult>> futures;
        
        ImportContext(ImportHistory importHistory, int totalItems) {
            this.importHistory = importHistory;
            this.startTime = System.currentTimeMillis();
            this.totalItems = totalItems;
            this.processedItems = new AtomicInteger(0);
            this.completed = false;
            this.workerCount = 1; 
            this.executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
            this.scalingScheduler = Executors.newSingleThreadScheduledExecutor();
            this.scalingInProgress = new AtomicBoolean(false);
            this.futures = new ArrayList<>();
        }
    }
    
    private static class ProcessingResult {
        int index;
        boolean success;
        String errorMessage;
        String name;
        Coordinates coordinates;
        String description;
        lab1.model.Difficulty difficulty;
        Discipline discipline;
        Integer minimalPoint;
        Person author;
        
        ProcessingResult(int index, boolean success, String errorMessage, String name, Coordinates coordinates,
                        String description, lab1.model.Difficulty difficulty, Discipline discipline, 
                        Integer minimalPoint, Person author) {
            this.index = index;
            this.success = success;
            this.errorMessage = errorMessage;
            this.name = name;
            this.coordinates = coordinates;
            this.description = description;
            this.difficulty = difficulty;
            this.discipline = discipline;
            this.minimalPoint = minimalPoint;
            this.author = author;
        }
    }
    
    public String importLabWorksFromJson(String jsonContent, String username) throws Exception {
        if (activeImports.size() >= MAX_CONCURRENT_IMPORTS) {
            logger.warning("Maximum concurrent imports reached (" + MAX_CONCURRENT_IMPORTS + ")");
            throw new RuntimeException("Maximum concurrent imports reached. Please try again later.");
        }
        
        String operationId = UUID.randomUUID().toString();
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            LabWorkDTO[] labWorkDTOs = mapper.readValue(jsonContent, LabWorkDTO[].class);
            
            logger.info("Import " + operationId + " starting with 1 worker for " + labWorkDTOs.length + " items");
            
            ImportHistory importHistory = new ImportHistory(operationId, "IN_PROGRESS", username);
            importHistoryDAO.create(importHistory);
            
            ImportContext context = new ImportContext(importHistory, labWorkDTOs.length);
            activeImports.put(operationId, context);
            
            scheduleScalingCheck(operationId);
            
            processImportWithScalablePool(labWorkDTOs, operationId);
            
            return operationId;
        } catch (Exception e) {
            activeImports.remove(operationId);
            
            logger.log(Level.SEVERE, "Import " + operationId + " failed to start: " + e.getMessage(), e);
            
            throw new RuntimeException("Import failed to start: " + e.getMessage(), e);
        }
    }
    
    private void scheduleScalingCheck(String operationId) {
        ImportContext context = activeImports.get(operationId);
        if (context == null) return;
        
        context.scalingScheduler.schedule(() -> {
            ImportContext ctx = activeImports.get(operationId);
            if (ctx != null && !ctx.completed && ctx.workerCount == 1 && !ctx.scalingCompleted) {
                logger.info("Import " + operationId + " taking more than 3 seconds, scaling up to 3 workers");
                scaleUpWorkers(operationId, 3);
            }
        }, 3, TimeUnit.SECONDS);
    }
    
    private void scaleUpWorkers(String operationId, int newWorkerCount) {
        ImportContext context = activeImports.get(operationId);
        if (context == null) return;
        
        synchronized (context.scalingLock) {
            if (context.scalingInProgress.get() || context.scalingCompleted) {
                return; 
            }
            
            context.scalingInProgress.set(true);
        }
        
        try {
            logger.info("Scaling import " + operationId + " from " + context.workerCount + " to " + newWorkerCount + " workers");
            
            
            context.executorService.setMaximumPoolSize(newWorkerCount);
            context.executorService.setCorePoolSize(newWorkerCount);
            context.workerCount = newWorkerCount;
            context.scalingCompleted = true;
            
            logger.info("Successfully scaled import " + operationId + " to " + newWorkerCount + " workers");
        } finally {
            context.scalingInProgress.set(false);
        }
    }
    
    private void processImportWithScalablePool(LabWorkDTO[] labWorkDTOs, String operationId) {
        ImportContext context = activeImports.get(operationId);
        if (context == null) return;
        
        try {
            List<ProcessingResult> validationResults = new ArrayList<>();
            List<String> validationErrors = new ArrayList<>();
            
            for (int i = 0; i < labWorkDTOs.length; i++) {
                try {
                    validateLabWorkDTO(labWorkDTOs[i]);
                    validationResults.add(new ProcessingResult(
                        i, true, null, labWorkDTOs[i].getName(), null, null, null, null, null, null
                    ));
                } catch (Exception e) {
                    validationErrors.add("Item " + i + ": " + e.getMessage());
                    validationResults.add(new ProcessingResult(
                        i, false, e.getMessage(), null, null, null, null, null, null, null
                    ));
                }
            }
            
            if (!validationErrors.isEmpty()) {
                String errorMessage = "Validation failed: " + String.join("; ", validationErrors);
                logger.severe("Import " + operationId + " failed validation: " + errorMessage);
                handleError(operationId, new RuntimeException(errorMessage));
                activeImports.remove(operationId);
                return;
            }
            
            for (int i = 0; i < labWorkDTOs.length; i++) {
                final int index = i;
                final LabWorkDTO dto = labWorkDTOs[i];
                
                CompletableFuture<ProcessingResult> future = CompletableFuture.supplyAsync(
                    () -> processLabWorkItem(index, dto, context.workerCount), 
                    context.executorService
                );
                
                context.futures.add(future);
            }
            
            CompletableFuture<Void> allDone = CompletableFuture.allOf(context.futures.toArray(new CompletableFuture[0]));
            allDone.thenRun(() -> {
                try {
                    completeImport(operationId, context.futures);
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Failed to complete import " + operationId, e);
                    handleError(operationId, e);
                } finally {
                    context.executorService.shutdown();
                    context.scalingScheduler.shutdown();
                }
            });
        } catch (Exception e) {
            context.executorService.shutdown();
            context.scalingScheduler.shutdown();
            throw e;
        }
    }
    
    private ProcessingResult processLabWorkItem(int index, LabWorkDTO dto, int workerCount) {
        try {
            logger.info(Thread.currentThread().getName() + " started processing item " + index + 
                       " with " + workerCount + " workers");
            try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warning("Import delay interrupted for item " + index);
                }
            Coordinates coordinates = new Coordinates(
                dto.getCoordinates().getX(),
                dto.getCoordinates().getY()
            );
            
            Discipline discipline = new Discipline(
                dto.getDiscipline().getName(),
                dto.getDiscipline().getLectureHours()
            );
            
            Person author = null;
            if (dto.getAuthor() != null) {
                Location location = new Location(
                    dto.getAuthor().getLocation().getX(),
                    dto.getAuthor().getLocation().getY(),
                    dto.getAuthor().getLocation().getZ()
                );
                
                author = new Person(
                    dto.getAuthor().getName(),
                    dto.getAuthor().getEyeColor(),
                    dto.getAuthor().getHairColor(),
                    location,
                    dto.getAuthor().getHeight(),
                    dto.getAuthor().getPassportID()
                );
            }
            
            return new ProcessingResult(index, true, null, dto.getName(), coordinates, dto.getDescription(),
                                      dto.getDifficulty(), discipline, dto.getMinimalPoint(), author);
        } catch (Exception e) {
            return new ProcessingResult(index, false, e.getMessage(), null, null, null, null, null, null, null);
        }
    }
    
    @Transactional
    private void completeImport(String operationId, List<CompletableFuture<ProcessingResult>> futures) {
        ImportContext context = activeImports.get(operationId);
        if (context == null) {
            logger.severe("Import context not found for operation: " + operationId);
            return;
        }
        
        try {
            List<ProcessingResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
            
            int successCount = 0;
            List<String> errors = new ArrayList<>();
            
            for (ProcessingResult result : results) {
                if (!result.success) {
                    errors.add("Item " + result.index + ": " + result.errorMessage);
                }
            }
            
            if (!errors.isEmpty()) {
                throw new RuntimeException("Processing failed: " + String.join("; ", errors));
            }
            
            for (ProcessingResult result : results) {
                
                if (isNameAlreadyExists(result.name)) {
                    errors.add("Item " + result.index + ": LabWork with name '" + result.name + "' already exists");
                    continue;
                }
                
                LabWork labWork = new LabWork(
                    result.name,
                    result.coordinates,
                    result.description,
                    result.difficulty,
                    result.discipline,
                    result.minimalPoint,
                    result.author
                );
                
                labWorkDAO.create(labWork);
                successCount++;
                
                int processed = context.processedItems.incrementAndGet();
                
                logger.info("Processed item " + result.index + " in import " + operationId + 
                           " (" + processed + "/" + context.totalItems + ") with " + context.workerCount + " workers");
            }
            
            if (!errors.isEmpty()) {
                throw new RuntimeException("Constraint validation failed: " + String.join("; ", errors));
            }
            
            long duration = System.currentTimeMillis() - context.startTime;
            
            logPerformanceMetrics(operationId, duration, context.workerCount, context.totalItems);
            
            context.importHistory.setStatus("SUCCESS");
            context.importHistory.setObjectsCount(successCount);
            importHistoryDAO.update(context.importHistory);
            context.completed = true;
            
            logger.info("Import " + operationId + " completed successfully with " + successCount + " items");
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error completing import " + operationId, e);
            handleError(operationId, e);
            
            throw new RuntimeException("Import failed: " + e.getMessage(), e);
        } finally {
            activeImports.remove(operationId);
        }
    }
    
    private void handleError(String operationId, Exception e) {
        ImportContext context = activeImports.get(operationId);
        if (context != null) {
            try {
                context.importHistory.setStatus("FAILED");
                context.importHistory.setErrorMessage(e.getMessage());
                importHistoryDAO.update(context.importHistory);
            } catch (Exception updateException) {
                logger.log(Level.SEVERE, "Failed to update import history for operation " + operationId, updateException);
            } finally {
                context.executorService.shutdown();
                context.scalingScheduler.shutdown();
                activeImports.remove(operationId);
            }
        }
    }
    
    private void logPerformanceMetrics(String operationId, long duration, int workerCount, int itemCount) {
        double avgPerItem = (double) duration / itemCount;
        logger.info(String.format(
            "Import Performance - OperationId: %s, Duration: %d ms, Workers: %d, Items: %d, AvgPerItem: %.2f ms",
            operationId, duration, workerCount, itemCount, avgPerItem
        ));
    }
    
    private void validateLabWorkDTO(LabWorkDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        if (dto.getCoordinates() == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        
        if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        
        if (dto.getDiscipline() == null) {
            throw new IllegalArgumentException("Discipline cannot be null");
        }
        
        if (dto.getMinimalPoint() == null || dto.getMinimalPoint() <= 0) {
            throw new IllegalArgumentException("Minimal point must be greater than 0");
        }
    }
    
    private boolean isNameAlreadyExists(String name) {
        return labWorkDAO.findByName(name) != null;
    }
    
    public List<ImportHistory> getImportHistory(String username, boolean isAdmin) {
        if (isAdmin) {
            return importHistoryDAO.findAll();
        } else {
            return importHistoryDAO.findByUsername(username);
        }
    }
    
    public ImportHistory getImportHistoryByOperationId(String operationId) {
        return importHistoryDAO.findByOperationId(operationId);
    }
    
    public int getActiveImportCount() {
        return activeImports.size();
    }
    
    public ImportProgress getImportProgress(String operationId) {
        ImportContext context = activeImports.get(operationId);
        if (context != null) {
            return new ImportProgress(
                context.processedItems.get(),
                context.totalItems,
                (int) ((context.processedItems.get() * 100.0) / context.totalItems)
            );
        }
        return null;
    }
     
    public static class ImportProgress {
        public final int processedItems;
        public final int totalItems;
        public final int percentage;
        
        public ImportProgress(int processedItems, int totalItems, int percentage) {
            this.processedItems = processedItems;
            this.totalItems = totalItems;
            this.percentage = percentage;
        }
    }
}