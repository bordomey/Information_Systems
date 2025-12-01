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
        try {
            List<LabWork> labWorks = labWorkService.getAllLabWorks();
            return Response.ok(labWorks).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving lab works: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/{id}")
    public Response getLabWorkById(@PathParam("id") int id) {
        try {
            LabWork labWork = labWorkService.getLabWorkById(id);
            if (labWork == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("LabWork with ID " + id + " not found").build();
            }
            return Response.ok(labWork).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving lab work: " + e.getMessage()).build();
        }
    }
    
    @POST
    public Response createLabWork(LabWorkDTO labWorkDTO) {
        try {
            // Validate required fields
            if (labWorkDTO.getName() == null || labWorkDTO.getName().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Name cannot be null or empty").build();
            }
            
            if (labWorkDTO.getCoordinates() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Coordinates cannot be null").build();
            }
            
            if (labWorkDTO.getDescription() == null || labWorkDTO.getDescription().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Description cannot be null or empty").build();
            }
            
            if (labWorkDTO.getDiscipline() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Discipline cannot be null").build();
            }
            
            if (labWorkDTO.getMinimalPoint() == null || labWorkDTO.getMinimalPoint() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Minimal point must be greater than 0").build();
            }
            
            // Convert DTO to entities
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
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating lab work: " + e.getMessage()).build();
        }
    }
    
    @PUT
    @Path("/{id}")
    public Response updateLabWork(@PathParam("id") int id, LabWorkDTO labWorkDTO) {
        try {
            // Validate required fields
            if (labWorkDTO.getName() == null || labWorkDTO.getName().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Name cannot be null or empty").build();
            }
            
            if (labWorkDTO.getCoordinates() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Coordinates cannot be null").build();
            }
            
            if (labWorkDTO.getDescription() == null || labWorkDTO.getDescription().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Description cannot be null or empty").build();
            }
            
            if (labWorkDTO.getDiscipline() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Discipline cannot be null").build();
            }
            
            if (labWorkDTO.getMinimalPoint() == null || labWorkDTO.getMinimalPoint() <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Minimal point must be greater than 0").build();
            }
            
            // Convert DTO to entities
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
                        .entity("LabWork with ID " + id + " not found").build();
            }
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Validation error: " + e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating lab work: " + e.getMessage()).build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    public Response deleteLabWork(@PathParam("id") int id) {
        try {
            boolean deleted = labWorkService.deleteLabWork(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("LabWork with ID " + id + " not found").build();
            }
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting lab work: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/average")
    public Response getAverageMinimalPoint() {
        try {
            double average = labWorkService.calculateAverageMinimalPoint();
            return Response.ok(average).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error calculating average: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/filter")
    public Response filterByDescription(@QueryParam("description") String description) {
        try {
            List<LabWork> labWorks = labWorkService.filterByDescriptionSubstring(description);
            return Response.ok(labWorks).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error filtering lab works: " + e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/top10-difficult")
    public Response addTop10MostDifficultToDiscipline(LabWorkDTO.DisciplineDTO disciplineDTO) {
        try {
            if (disciplineDTO.getName() == null || disciplineDTO.getName().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Discipline name cannot be null or empty").build();
            }
            
            Discipline discipline = new Discipline(
                disciplineDTO.getName(),
                disciplineDTO.getLectureHours()
            );

            labWorkService.addTop10MostDifficultToDiscipline(discipline);
            
            return Response.ok("Successfully updated top 10 most difficult lab works with discipline: " + discipline.getName()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating lab works: " + e.getMessage()).build();
        }
    }
    
    @GET
    @Path("/filter-by-author")
    public Response filterByAuthorGreaterThan(@QueryParam("authorName") String authorName) {
        try {
            if (authorName == null || authorName.isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Author name cannot be null or empty").build();
            }
            
            List<LabWork> labWorks = labWorkService.filterByAuthorGreaterThan(authorName);
            return Response.ok(labWorks).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error filtering lab works: " + e.getMessage()).build();
        }
    }
    
    @POST
    @Path("/{id}/decrease-difficulty")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response decreaseDifficulty(@PathParam("id") int id, Map<String, Integer> requestBody) {
        try {
            Integer steps = requestBody.get("steps");
            if (steps == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Steps parameter is required").build();
            }
            
            if (steps < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Steps must be a non-negative integer").build();
            }
            
            boolean success = labWorkService.decreaseDifficulty(id, steps);
            if (!success) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("LabWork with ID " + id + " not found or has no difficulty set").build();
            }
            
            return Response.ok("Successfully decreased difficulty by " + steps + " steps").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error decreasing difficulty: " + e.getMessage()).build();
        }
    }
}