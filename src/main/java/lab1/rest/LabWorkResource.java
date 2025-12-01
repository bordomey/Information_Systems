package lab1.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab1.model.LabWork;
import lab1.model.Coordinates;
import lab1.model.Discipline;
import lab1.model.Person;
import lab1.model.Location;
import lab1.rest.dtos.LabWorkDTO;
import lab1.service.LabWorkService;
import java.util.List;
import java.util.Map;

@Path("/labworks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LabWorkResource {
    
    @Inject
    private LabWorkService labWorkService;
    
    @GET
    public Response getAllLabWorks() {
        List<LabWork> labWorks = labWorkService.getAllLabWorks();
        return Response.ok(labWorks).build();
    }
    
    @GET
    @Path("/{id}")
    public Response getLabWorkById(@PathParam("id") int id) {
        LabWork labWork = labWorkService.getLabWorkById(id);
        if (labWork == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"LabWork with ID " + id + " not found\"}").build();
        }
        return Response.ok(labWork).build();
    }
    
    @POST
    public Response createLabWork(LabWorkDTO labWorkDTO) {
        if (labWorkDTO.getName() == null || labWorkDTO.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Name cannot be null or empty\"}").build();
        }
        
        if (labWorkDTO.getCoordinates() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Coordinates cannot be null\"}").build();
        }
        
        if (labWorkDTO.getDescription() == null || labWorkDTO.getDescription().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Description cannot be null or empty\"}").build();
        }
        
        if (labWorkDTO.getDiscipline() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Discipline cannot be null\"}").build();
        }
        
        if (labWorkDTO.getMinimalPoint() == null || labWorkDTO.getMinimalPoint() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Minimal point must be greater than 0\"}").build();
        }
        
        Coordinates coordinates = new Coordinates(
            labWorkDTO.getCoordinates().getX(),
            labWorkDTO.getCoordinates().getY()
        );
        
        Discipline discipline = new Discipline(
            labWorkDTO.getDiscipline().getName(),
            labWorkDTO.getDiscipline().getLectureHours()
        );
        
        Person author = null;
        if (labWorkDTO.getAuthor() != null) {
            Location location = new Location(
                labWorkDTO.getAuthor().getLocation().getX(),
                labWorkDTO.getAuthor().getLocation().getY(),
                labWorkDTO.getAuthor().getLocation().getZ()
            );
            
            author = new Person(
                labWorkDTO.getAuthor().getName(),
                labWorkDTO.getAuthor().getEyeColor(),
                labWorkDTO.getAuthor().getHairColor(),
                location,
                labWorkDTO.getAuthor().getHeight(),
                labWorkDTO.getAuthor().getPassportID()
            );
        }
        
        LabWork created = labWorkService.createLabWork(
            labWorkDTO.getName(),
            coordinates,
            labWorkDTO.getDescription(),
            labWorkDTO.getDifficulty(),
            discipline,
            labWorkDTO.getMinimalPoint(),
            author
        );
        return Response.status(Response.Status.CREATED).entity(created).build();
    }
    
    @PUT
    @Path("/{id}")
    public Response updateLabWork(@PathParam("id") int id, LabWorkDTO labWorkDTO) {
        if (labWorkDTO.getName() == null || labWorkDTO.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Name cannot be null or empty\"}").build();
        }
        
        if (labWorkDTO.getCoordinates() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Coordinates cannot be null\"}").build();
        }
        
        if (labWorkDTO.getDescription() == null || labWorkDTO.getDescription().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Description cannot be null or empty\"}").build();
        }
        
        if (labWorkDTO.getDiscipline() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Discipline cannot be null\"}").build();
        }
        
        if (labWorkDTO.getMinimalPoint() == null || labWorkDTO.getMinimalPoint() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Minimal point must be greater than 0\"}").build();
        }
        
        Coordinates coordinates = new Coordinates(
            labWorkDTO.getCoordinates().getX(),
            labWorkDTO.getCoordinates().getY()
        );
        
        Discipline discipline = new Discipline(
            labWorkDTO.getDiscipline().getName(),
            labWorkDTO.getDiscipline().getLectureHours()
        );
        
        Person author = null;
        if (labWorkDTO.getAuthor() != null) {
            Location location = new Location(
                labWorkDTO.getAuthor().getLocation().getX(),
                labWorkDTO.getAuthor().getLocation().getY(),
                labWorkDTO.getAuthor().getLocation().getZ()
            );
            
            author = new Person(
                labWorkDTO.getAuthor().getName(),
                labWorkDTO.getAuthor().getEyeColor(),
                labWorkDTO.getAuthor().getHairColor(),
                location,
                labWorkDTO.getAuthor().getHeight(),
                labWorkDTO.getAuthor().getPassportID()
            );
        }
        
        LabWork updated = labWorkService.updateLabWork(
            id,
            labWorkDTO.getName(),
            coordinates,
            labWorkDTO.getDescription(),
            labWorkDTO.getDifficulty(),
            discipline,
            labWorkDTO.getMinimalPoint(),
            author
        );
        if (updated == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"LabWork with ID " + id + " not found\"}").build();
        }
        return Response.ok(updated).build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteLabWork(@PathParam("id") int id) {
        boolean deleted = labWorkService.deleteLabWork(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"LabWork with ID " + id + " not found\"}").build();
        }
        return Response.noContent().build();
    }
    
    @GET
    @Path("/average")
    public Response getAverageMinimalPoint() {
        double average = labWorkService.calculateAverageMinimalPoint();
        return Response.ok(average).build();
    }
    
    @GET
    @Path("/filter")
    public Response filterByDescription(@QueryParam("description") String description) {
        List<LabWork> labWorks = labWorkService.filterByDescriptionSubstring(description);
        return Response.ok(labWorks).build();
    }
    
    @POST
    @Path("/top10-difficult")
    public Response addTop10MostDifficultToDiscipline(LabWorkDTO.DisciplineDTO disciplineDTO) {
        if (disciplineDTO.getName() == null || disciplineDTO.getName().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Discipline name cannot be null or empty\"}").build();
        }
        
        Discipline discipline = new Discipline(
            disciplineDTO.getName(),
            disciplineDTO.getLectureHours()
        );

        labWorkService.addTop10MostDifficultToDiscipline(discipline);
        
        return Response.ok("{\"message\": \"Successfully updated top 10 most difficult lab works with discipline: " + discipline.getName() + "\"}").build();
    }
    
    @GET
    @Path("/filter-by-author")
    public Response filterByAuthorGreaterThan(@QueryParam("authorName") String authorName) {
        if (authorName == null || authorName.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Author name cannot be null or empty\"}").build();
        }
        
        List<LabWork> labWorks = labWorkService.filterByAuthorGreaterThan(authorName);
        return Response.ok(labWorks).build();
    }
    
    @POST
    @Path("/{id}/decrease-difficulty")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response decreaseDifficulty(@PathParam("id") int id, Map<String, Integer> requestBody) {
        Integer steps = requestBody.get("steps");
        if (steps == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Steps parameter is required\"}").build();
        }
        
        if (steps < 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Steps must be a non-negative integer\"}").build();
        }
        
        boolean success = labWorkService.decreaseDifficulty(id, steps);
        if (!success) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"LabWork with ID " + id + " not found or has no difficulty set\"}").build();
        }
        
        return Response.ok("{\"message\": \"Successfully decreased difficulty by " + steps + " steps\"}").build();
    }
}