package lab1.util;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import lab1.model.User;
import lab1.service.UserService;
import java.util.logging.Logger;

@Singleton
@Startup
public class InitializeUsers {
    
    private static final Logger logger = Logger.getLogger(InitializeUsers.class.getName());
    
    @Inject
    private UserService userService;
    
    @PostConstruct
    public void init() {
        try {
            if (userService.getUserByUsername("admin") == null) {
                User admin = userService.registerUser("admin", "admin", "admin");
                logger.info("Created default admin user: " + admin.getUsername());
            }
            
            if (userService.getUserByUsername("user") == null) {
                User user = userService.registerUser("user", "user", "user");
                logger.info("Created default user: " + user.getUsername());
            }
            
            logger.info("User initialization completed");
        } catch (Exception e) {
            logger.severe("Failed to initialize users: " + e.getMessage());
        }
    }
}