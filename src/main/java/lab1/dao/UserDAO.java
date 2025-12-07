package lab1.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lab1.model.User;
import java.util.List;

@Stateless
public class UserDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public User create(User user) {
        entityManager.persist(user);
        return user;
    }
    
    public User findById(Long id) {
        return entityManager.find(User.class, id);
    }
    
    public User findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }
    
    public List<User> findAll() {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }
    
    public User update(User user) {
        return entityManager.merge(user);
    }
    
    public void delete(Long id) {
        User user = findById(id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
    
    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }
}