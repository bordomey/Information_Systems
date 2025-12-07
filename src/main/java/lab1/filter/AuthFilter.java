package lab1.filter;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lab1.service.UserService;
import lab1.model.User;
import java.io.IOException;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.logging.Logger;

@Provider
public class AuthFilter implements ContainerRequestFilter {
    
    private static final Logger logger = Logger.getLogger(AuthFilter.class.getName());
    
    @Inject
    private UserService userService;
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        if (requestContext.getRequest().getMethod().equals("OPTIONS")) {
            return;
        }
        
        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("auth/")) {
            return;
        }
        
        String authHeader = requestContext.getHeaderString("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String userName = requestContext.getHeaderString("X-User-Name");
            if (userName != null && !userName.isEmpty()) {
                requestContext.setProperty("username", userName);
                requestContext.setProperty("role", requestContext.getHeaderString("X-User-Role") != null ? 
                    requestContext.getHeaderString("X-User-Role") : "user");
                return;
            }
            
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Authentication required\"}")
                    .build()
            );
            return;
        }
        
        String token = authHeader.substring("Bearer ".length()).trim();
        
        try {
            String decodedToken = new String(Base64.getDecoder().decode(token));
            StringTokenizer tokenizer = new StringTokenizer(decodedToken, ":");
            
            if (tokenizer.countTokens() < 2) {
                throw new Exception("Invalid token format");
            }
            
            String username = tokenizer.nextToken();
            String timestampStr = tokenizer.nextToken();
            
            long timestamp = Long.parseLong(timestampStr);
            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > 24 * 60 * 60 * 1000) {
                throw new Exception("Token expired");
            }
            
            User user = userService.getUserByUsername(username);
            if (user == null) {
                throw new Exception("User not found");
            }
            
            requestContext.setProperty("username", user.getUsername());
            requestContext.setProperty("role", user.getRole());
            
        } catch (Exception e) {
            logger.warning("Authentication failed: " + e.getMessage());
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid or expired token\"}")
                    .build()
            );
        }
    }
}