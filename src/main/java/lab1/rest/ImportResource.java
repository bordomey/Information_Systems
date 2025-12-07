package lab1.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.container.ContainerRequestContext;
import lab1.service.ImportService;
import lab1.model.ImportHistory;
import lab1.service.ImportService.ImportProgress;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

@Path("/import")
public class ImportResource {
    
    private static final Logger logger = Logger.getLogger(ImportResource.class.getName());
    
    @Inject
    private ImportService importService;
    
    @OPTIONS
    @Path("/labworks")
    public Response preflightImport() {
        return Response.ok().build();
    }
    
    @POST
    @Path("/labworks")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response importLabWorksFromJson(MultipartFormDataInput input, 
                                         @Context ContainerRequestContext requestContext) {
        String username = (String) requestContext.getProperty("username");
        try {
            if (username == null || username.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Authentication required to import files\"}").build();
            }
            
            Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
            List<InputPart> inputParts = uploadForm.get("file");
            
            if (inputParts == null || inputParts.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"File is required\"}").build();
            }
            
            InputPart inputPart = inputParts.get(0);
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            String jsonContent = getStringFromInputStream(inputStream);
            
            logger.info("Starting import for user: " + username);
            String operationId = importService.importLabWorksFromJson(jsonContent, username);
            logger.info("Import initiated successfully with operation ID: " + operationId);
            
            return Response.status(Response.Status.CREATED)
                .entity("{\"operationId\": \"" + operationId + "\", \"message\": \"Import started successfully\"}").build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Import failed: " + e.getMessage(), e);
            
            if (e.getMessage().contains("Maximum concurrent imports reached")) {
                return Response.status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
            }
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    @OPTIONS
    @Path("/progress/{operationId}")
    public Response preflightProgress() {
        return Response.ok().build();
    }
    
    @GET
    @Path("/progress/{operationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportProgress(@PathParam("operationId") String operationId) {
        try {
            ImportProgress progress = importService.getImportProgress(operationId);
            if (progress == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Import operation not found or completed\"}").build();
            }
            
            return Response.ok(progress).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get import progress: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    private String getStringFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return new String(buffer.toByteArray(), "UTF-8");
    }
    
    @OPTIONS
    @Path("/history")
    public Response preflightHistory() {
        return Response.ok().build();
    }
    
    @GET
    @Path("/history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportHistory(@Context ContainerRequestContext requestContext) {
        String username = (String) requestContext.getProperty("username");
        String userRole = (String) requestContext.getProperty("role");
        try {
            if (username == null || username.isEmpty()) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Authentication required to view import history\"}").build();
            }
            
            boolean isAdmin = "admin".equalsIgnoreCase(userRole);
            List<ImportHistory> history = importService.getImportHistory(username, isAdmin);
            return Response.ok(history).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get import history: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
    
    @OPTIONS
    @Path("/history/{operationId}")
    public Response preflightHistoryById() {
        return Response.ok().build();
    }
    
    @GET
    @Path("/history/{operationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportHistoryByOperationId(@PathParam("operationId") String operationId) {
        try {
            ImportHistory history = importService.getImportHistoryByOperationId(operationId);
            if (history == null) {
                return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Import history not found for operation ID: " + operationId + "\"}").build();
            }
            return Response.ok(history).build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to get import history by operation ID: " + e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        }
    }
}