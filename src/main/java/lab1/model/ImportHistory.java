package lab1.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "import_history")
public class ImportHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "operation_id", nullable = false, unique = true)
    private String operationId;
    
    @Column(name = "status", nullable = false)
    private String status; // SUCCESS, FAILED, IN_PROGRESS
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "objects_count")
    private Integer objectsCount;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "timestamp", nullable = false)
    private Date timestamp;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    public ImportHistory() {
        this.timestamp = new Date();
    }
    
    public ImportHistory(String operationId, String status, String username) {
        this.operationId = operationId;
        this.status = status;
        this.username = username;
        this.timestamp = new Date();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOperationId() {
        return operationId;
    }
    
    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public Integer getObjectsCount() {
        return objectsCount;
    }
    
    public void setObjectsCount(Integer objectsCount) {
        this.objectsCount = objectsCount;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}