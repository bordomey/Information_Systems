package lab1.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lab1.model.LabWork;
import java.util.List;

@Stateless
public class LabWorkDAO {
    
    @PersistenceContext(unitName = "LabWorkPU")
    private EntityManager entityManager;
    
    public LabWork create(LabWork labWork) {
        entityManager.persist(labWork);
        return labWork;
    }
    
    public LabWork findById(int id) {
        return entityManager.find(LabWork.class, id);
    }
    
    public List<LabWork> findAll() {
        TypedQuery<LabWork> query = entityManager.createQuery("SELECT l FROM LabWork l", LabWork.class);
        return query.getResultList();
    }
    
    public LabWork update(LabWork labWork) {
        return entityManager.merge(labWork);
    }
    
    public void delete(int id) {
        LabWork labWork = findById(id);
        if (labWork != null) {
            entityManager.remove(labWork);
        }
    }
    
    public List<LabWork> findByDescriptionSubstring(String substring) {
        TypedQuery<LabWork> query = entityManager.createQuery(
            "SELECT l FROM LabWork l WHERE l.description LIKE :substring", LabWork.class);
        query.setParameter("substring", "%" + substring + "%");
        return query.getResultList();
    }
    
    public double calculateAverageMinimalPoint() {
        TypedQuery<Double> query = entityManager.createQuery(
            "SELECT AVG(l.minimalPoint) FROM LabWork l", Double.class);
        Double result = query.getSingleResult();
        return result != null ? result : 0.0;
    }
    

    public List<LabWork> findByAuthorGreaterThan(String authorName) {
        TypedQuery<LabWork> query = entityManager.createQuery(
            "SELECT l FROM LabWork l WHERE l.author.name > :authorName", LabWork.class);
        query.setParameter("authorName", authorName);
        return query.getResultList();
    }
    
}