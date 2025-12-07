package lab1.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lab1.dao.UserDAO;
import lab1.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class UserService {
    
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    
    @Inject
    private UserDAO userDAO;
    
    public User authenticate(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return null;
        }
        
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }
        
        String hashedPassword = hashPassword(password);
        if (hashedPassword != null && hashedPassword.equals(user.getPasswordHash())) {
            user.setLastLogin(new Date());
            userDAO.update(user);
            return user;
        }
        
        return null;
    }
    
    public User registerUser(String username, String password, String role) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        
        if (userDAO.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        String hashedPassword = hashPassword(password);
        if (hashedPassword == null) {
            throw new RuntimeException("Failed to hash password");
        }
        
        User user = new User(username, hashedPassword, role);
        return userDAO.create(user);
    }
    
    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.severe("Failed to hash password: " + e.getMessage());
            return null;
        }
    }
}