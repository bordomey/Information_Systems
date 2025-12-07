package lab1.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import lab1.dao.LabWorkDAO;
import lab1.model.LabWork;
import lab1.model.Coordinates;
import lab1.model.Difficulty;
import lab1.model.Discipline;
import lab1.model.Person;
import java.util.List;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

@Stateless
public class LabWorkService {
    
    private static final Logger logger = Logger.getLogger(LabWorkService.class.getName());
    
    @Inject
    private LabWorkDAO labWorkDAO;
    
    @Transactional(value = TxType.REQUIRES_NEW)
    public LabWork createLabWork(String name, Coordinates coordinates, String description,
                                 Difficulty difficulty, Discipline discipline, 
                                 Integer minimalPoint, Person author) {
        
        logger.info("Starting createLabWork transaction for name: " + name);
        
        if (name == null || name.isEmpty()) {
            logger.warning("Validation failed: Name cannot be null or empty");
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (coordinates == null) {
            logger.warning("Validation failed: Coordinates cannot be null");
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (description == null || description.isEmpty()) {
            logger.warning("Validation failed: Description cannot be null or empty");
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (discipline == null) {
            logger.warning("Validation failed: Discipline cannot be null");
            throw new IllegalArgumentException("Discipline cannot be null");
        }
        if (minimalPoint == null || minimalPoint <= 0) {
            logger.warning("Validation failed: Minimal point must be greater than 0");
            throw new IllegalArgumentException("Minimal point must be greater than 0");
        }

        try {
            // Check for existing name with pessimistic lock to prevent race conditions
            logger.info("Checking for existing LabWork with name: " + name + " using pessimistic lock");
            LabWork existing = labWorkDAO.findByNameWithLock(name);
            if (existing != null) {
                logger.warning("Duplicate name detected: LabWork with name '" + name + "' already exists");
                throw new IllegalArgumentException("LabWork with name '" + name + "' already exists");
            }
            
            logger.info("Creating new LabWork with name: " + name);
            LabWork labWork = new LabWork(name, coordinates, description, difficulty, discipline, minimalPoint, author);
            LabWork result = labWorkDAO.create(labWork);
            logger.info("Successfully created LabWork with ID: " + result.getId() + " and name: " + name);
            return result;
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Database error during LabWork creation: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create LabWork due to database error", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error during LabWork creation: " + e.getMessage(), e);
            throw e;
        }
    }

    public LabWork getLabWorkById(int id) {
        return labWorkDAO.findById(id);
    }
    @Transactional(value = TxType.REQUIRES_NEW)
    public LabWork updateLabWork(int id, String name, Coordinates coordinates, String description,
                                 Difficulty difficulty, Discipline discipline, 
                                 Integer minimalPoint, Person author) {
        logger.info("Starting updateLabWork for ID: " + id + " with new name: " + name);
        
        LabWork labWork = getLabWorkById(id);
        if (labWork == null) {
            logger.warning("LabWork with ID " + id + " not found for update");
            return null;
        }

        if (name == null || name.isEmpty()) {
            logger.warning("Validation failed: Name cannot be null or empty");
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (coordinates == null) {
            logger.warning("Validation failed: Coordinates cannot be null");
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (description == null || description.isEmpty()) {
            logger.warning("Validation failed: Description cannot be null or empty");
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (discipline == null) {
            logger.warning("Validation failed: Discipline cannot be null");
            throw new IllegalArgumentException("Discipline cannot be null");
        }
        if (minimalPoint == null || minimalPoint <= 0) {
            logger.warning("Validation failed: Minimal point must be greater than 0");
            throw new IllegalArgumentException("Minimal point must be greater than 0");
        }
        
        try {
            // Check for name uniqueness if name is changing
            if (!name.equals(labWork.getName())) {
                logger.info("Name is changing, checking for duplicates: " + name);
                LabWork existing = labWorkDAO.findByNameWithLock(name);
                if (existing != null) {
                    logger.warning("Duplicate name detected during update: LabWork with name '" + name + "' already exists");
                    throw new IllegalArgumentException("LabWork with name '" + name + "' already exists");
                }
            }

            labWork.setName(name);
            labWork.setCoordinates(coordinates);
            labWork.setDescription(description);
            labWork.setDifficulty(difficulty);
            labWork.setDiscipline(discipline);
            labWork.setMinimalPoint(minimalPoint);
            labWork.setAuthor(author);
            
            LabWork result = labWorkDAO.update(labWork);
            logger.info("Successfully updated LabWork with ID: " + id);
            return result;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during LabWork update: " + e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(value = TxType.REQUIRES_NEW)
    public boolean deleteLabWork(int id) {
        logger.info("Attempting to delete LabWork with ID: " + id);
        LabWork labWork = getLabWorkById(id);
        if (labWork == null) {
            logger.warning("LabWork with ID " + id + " not found for deletion");
            return false;
        }
        labWorkDAO.delete(id);
        logger.info("Successfully deleted LabWork with ID: " + id);
        return true;
    }
    
    @Transactional(value = TxType.REQUIRES_NEW)
    public void deleteAllLabWorks() {
        logger.info("Deleting all LabWorks");
        labWorkDAO.deleteAll();
        logger.info("Successfully deleted all LabWorks");
    }

    @Transactional(value = TxType.REQUIRES_NEW) 
    public List<LabWork> getAllLabWorks() {
        logger.info("Retrieving all LabWorks");
        return labWorkDAO.findAll();
    }

    @Transactional(value = TxType.REQUIRES_NEW)
    public double calculateAverageMinimalPoint() {
        logger.info("Calculating average minimal point");
        return labWorkDAO.calculateAverageMinimalPoint();
    }

    @Transactional(value = TxType.REQUIRES_NEW)
    public List<LabWork> filterByDescriptionSubstring(String substring) {
        logger.info("Filtering LabWorks by description substring: " + substring);
        return labWorkDAO.findByDescriptionSubstring(substring);
    }

    @Transactional(value = TxType.REQUIRES_NEW)
    public void addTop10MostDifficultToDiscipline(Discipline discipline) {
        logger.info("Adding top 10 most difficult LabWorks to discipline: " + discipline.getName());

        List<LabWork> allLabWorks = labWorkDAO.findAll();

        allLabWorks.sort((lw1, lw2) -> {
            if (lw1.getDifficulty() == null && lw2.getDifficulty() == null) return 0;
            if (lw1.getDifficulty() == null) return 1;  
            if (lw2.getDifficulty() == null) return -1; 
            return Integer.compare(lw2.getDifficulty().ordinal(), lw1.getDifficulty().ordinal());
        });
        
        int count = Math.min(10, allLabWorks.size());
        for (int i = 0; i < count; i++) {
            LabWork labWork = allLabWorks.get(i);
            labWork.setDiscipline(discipline);
            labWorkDAO.update(labWork);
        }
        
        logger.info("Added " + count + " most difficult lab works to discipline: " + discipline.getName());
    }
    
    @Transactional(value = TxType.REQUIRES_NEW)     
    public List<LabWork> filterByAuthorGreaterThan(String authorName) {
        logger.info("Filtering LabWorks by author greater than: " + authorName);
        return labWorkDAO.findByAuthorGreaterThan(authorName);
    }
    
    @Transactional(value = TxType.REQUIRES_NEW)
    public boolean decreaseDifficulty(int id, int steps) {
        logger.info("Decreasing difficulty for LabWork ID: " + id + " by " + steps + " steps");
        LabWork labWork = getLabWorkById(id);
        if (labWork == null || labWork.getDifficulty() == null) {
            logger.warning("LabWork with ID " + id + " not found or has no difficulty set");
            return false;
        }

        Difficulty[] difficulties = Difficulty.values();
        int currentIndex = Arrays.asList(difficulties).indexOf(labWork.getDifficulty());
        int newIndex = Math.max(0, currentIndex - steps);
        labWork.setDifficulty(difficulties[newIndex]);
        
        labWorkDAO.update(labWork);
        logger.info("Successfully decreased difficulty for LabWork ID: " + id);
        return true;
    }
}