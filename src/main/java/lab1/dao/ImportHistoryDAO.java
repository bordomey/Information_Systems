package lab1.dao;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lab1.model.ImportHistory;
import java.util.List;

@Stateless
public class ImportHistoryDAO {
    
    @PersistenceContext(unitName = "LabWorkPU")
    private EntityManager entityManager;
    
    public ImportHistory create(ImportHistory importHistory) {
        entityManager.persist(importHistory);
        return importHistory;
    }
    
    public ImportHistory findById(Long id) {
        return entityManager.find(ImportHistory.class, id);
    }
    
    public ImportHistory findByOperationId(String operationId) {
        TypedQuery<ImportHistory> query = entityManager.createQuery(
            "SELECT ih FROM ImportHistory ih WHERE ih.operationId = :operationId", ImportHistory.class);
        query.setParameter("operationId", operationId);
        List<ImportHistory> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public List<ImportHistory> findAll() {
        TypedQuery<ImportHistory> query = entityManager.createQuery("SELECT ih FROM ImportHistory ih ORDER BY ih.timestamp DESC", ImportHistory.class);
        return query.getResultList();
    }
    
    public List<ImportHistory> findByUsername(String username) {
        TypedQuery<ImportHistory> query = entityManager.createQuery(
            "SELECT ih FROM ImportHistory ih WHERE ih.username = :username ORDER BY ih.timestamp DESC", ImportHistory.class);
        query.setParameter("username", username);
        return query.getResultList();
    }
    
    public ImportHistory update(ImportHistory importHistory) {
        return entityManager.merge(importHistory);
    }
    
    public void delete(Long id) {
        ImportHistory importHistory = findById(id);
        if (importHistory != null) {
            entityManager.remove(importHistory);
        }
    }
}