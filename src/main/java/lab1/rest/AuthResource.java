package lab1.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lab1.model.User;
import lab1.service.UserService;
import java.util.Base64;
import java.util.logging.Logger;

@Path("/auth")
public class AuthResource {
    
    private static final Logger logger = Logger.getLogger(AuthResource.class.getName());
    
    @Inject
    private UserService userService;
    
    @OPTIONS
    @Path("/login")
    public Response preflightLogin() {
        return Response.ok().build();
    }
    

    private String generateToken(String username) {
        String tokenData = username + ":" + System.currentTimeMillis();
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }
    
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest loginRequest) {
        try {
            if (loginRequest.getUsername() == null || loginRequest.getPassword() == null ||
                loginRequest.getUsername().isEmpty() || loginRequest.getPassword().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Username and password are required\"}").build();
            }
            
            User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"Invalid username or password\"}").build();
            }
            
            String token = generateToken(user.getUsername());
            
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), user.getRole());
            userInfo.setToken(token);
            return Response.ok(userInfo).build();
        } catch (Exception e) {
            logger.severe("Login failed: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Login failed\"}").build();
        }
    }
    
    @OPTIONS
    @Path("/register")
    public Response preflightRegister() {
        return Response.ok().build();
    }
    
    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest registerRequest) {
        try {
            if (registerRequest.getUsername() == null || registerRequest.getPassword() == null ||
                registerRequest.getUsername().isEmpty() || registerRequest.getPassword().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Username and password are required\"}").build();
            }
            
            String role = "user";
            User user = userService.registerUser(registerRequest.getUsername(), registerRequest.getPassword(), role);
            
            String token = generateToken(user.getUsername());
            
            UserInfo userInfo = new UserInfo(user.getId(), user.getUsername(), user.getRole());
            userInfo.setToken(token);
            return Response.status(Response.Status.CREATED)
                .entity(userInfo).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + e.getMessage() + "\"}").build();
        } catch (Exception e) {
            logger.severe("Registration failed: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"error\": \"Registration failed\"}").build();
        }
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        
        public LoginRequest() {}
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    public static class RegisterRequest {
        private String username;
        private String password;
        
        public RegisterRequest() {}
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;
        private String token;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public String getToken() {
            return token;
        }
        
        public void setToken(String token) {
            this.token = token;
        }
    }
}