package lab1.filter;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

@Provider
public class AuthFilter implements ContainerRequestFilter {
    
    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        return;
    }
}