package lab1.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.LockModeType;
import lab1.model.LabWork;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class LabWorkDAO {
    
    private static final Logger logger = Logger.getLogger(LabWorkDAO.class.getName());
    
    @PersistenceContext(unitName = "LabWorkPU")
    private EntityManager entityManager;
    
    public LabWork create(LabWork labWork) {
        logger.info("Creating new LabWork entity");
        entityManager.persist(labWork);
        logger.info("LabWork entity created with ID: " + labWork.getId());
        return labWork;
    }
    
    public LabWork findById(int id) {
        logger.fine("Finding LabWork by ID: " + id);
        return entityManager.find(LabWork.class, id);
    }
    
    public LabWork findByName(String name) {
        logger.fine("Finding LabWork by name: " + name);
        TypedQuery<LabWork> query = entityManager.createQuery(
            "SELECT l FROM LabWork l WHERE l.name = :name", LabWork.class);
        query.setParameter("name", name);
        List<LabWork> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public LabWork findByNameWithLock(String name) {
        logger.info("Finding LabWork by name with pessimistic lock: " + name);
        TypedQuery<LabWork> query = entityManager.createQuery(
            "SELECT l FROM LabWork l WHERE l.name = :name", LabWork.class);
        query.setParameter("name", name);
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        List<LabWork> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<LabWork> findAll() {
        logger.fine("Finding all LabWorks");
        TypedQuery<LabWork> query = entityManager.createQuery("SELECT l FROM LabWork l", LabWork.class);
        return query.getResultList();
    }
    
    public LabWork update(LabWork labWork) {
        logger.fine("Updating LabWork with ID: " + labWork.getId());
        LabWork merged = entityManager.merge(labWork);
        logger.fine("LabWork updated successfully");
        return merged;
    }
    
    public void delete(int id) {
        logger.fine("Deleting LabWork with ID: " + id);
        LabWork labWork = findById(id);
        if (labWork != null) {
            entityManager.remove(labWork);
            logger.fine("LabWork deleted successfully");
        } else {
            logger.warning("LabWork with ID " + id + " not found for deletion");
        }
    }
    
    public void deleteAll() {
        logger.info("Deleting all LabWorks");
        entityManager.createQuery("DELETE FROM LabWork").executeUpdate();
        logger.info("All LabWorks deleted successfully");
    }
    
    public List<LabWork> findByDescriptionSubstring(String substring) {
        logger.fine("Finding LabWorks by description substring: " + substring);
        TypedQuery<LabWork> query = entityManager.createQuery(
            "SELECT l FROM LabWork l WHERE l.description LIKE :substring", LabWork.class);
        query.setParameter("substring", "%" + substring + "%");
        return query.getResultList();
    }
    
    public double calculateAverageMinimalPoint() {
        logger.fine("Calculating average minimal point");
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT AVG(l.minimalPoint) FROM LabWork l", Double.class);
        Double result = query.getSingleResult();
        double avg = result != null ? result : 0.0;
        logger.fine("Average minimal point calculated: " + avg);
        return avg;
    }
    
    public List<LabWork> findByAuthorGreaterThan(String authorName) {
        logger.fine("Finding LabWorks by author greater than: " + authorName);
        TypedQuery<LabWork> query = entityManager.createQuery(
            "SELECT l FROM LabWork l WHERE l.author.name > :authorName", LabWork.class);
        query.setParameter("authorName", authorName);
        return query.getResultList();
    }
    
}