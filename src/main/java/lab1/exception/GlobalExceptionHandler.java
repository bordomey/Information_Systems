package lab1.exception;

import jakarta.json.stream.JsonParsingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable throwable) {
        Throwable cause = throwable;
        
        while (cause != null && !(cause instanceof JsonParsingException)) {
            cause = cause.getCause();
        }

        if (cause instanceof JsonParsingException) {
            JsonParsingException jsonParsingException = (JsonParsingException) cause;
            String errorMessage = "Invalid JSON :" + jsonParsingException.getMessage();
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }
        
        if (throwable instanceof WebApplicationException) {
            return handleWebApplicationException((WebApplicationException) throwable);
        }
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"An unexpected error occurred\"}")
                .build();
    }
    
    private Response handleWebApplicationException(WebApplicationException exception) {
        Response response = exception.getResponse();
        if (response.getStatusInfo().getFamily() == Response.Status.Family.SERVER_ERROR) {
            return Response.status(response.getStatus())
                    .entity("{\"error\": \"An unexpected error occurred\"}")
                    .build();
        }
        
        if (response.hasEntity()) {
            return response;
        } else {
            switch (response.getStatus()) {
                case 400:
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"error\": \"Bad Request\"}")
                            .build();
                case 404:
                    return Response.status(Response.Status.NOT_FOUND)
                            .entity("{\"error\": \"Resource not found\"}")
                            .build();
                case 405:
                    return Response.status(Response.Status.METHOD_NOT_ALLOWED)
                            .entity("{\"error\": \"Method not allowed\"}")
                            .build();
                case 415:
                    return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                            .entity("{\"error\": \"Unsupported media type\"}")
                            .build();
                default:
                    return Response.status(response.getStatus())
                            .entity("{\"error\": \"" + response.getStatusInfo().getReasonPhrase() + "\"}")
                            .build();
            }
        }
    }
}