package lab1.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lab_work")
public class LabWork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String name;
    
    @Embedded
    private Coordinates coordinates;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private Date creationDate;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "discipline_id", nullable = false)
    private Discipline discipline;
    
    @Column(name = "minimal_point", nullable = false)
    private Integer minimalPoint;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author_id")
    private Person author;

    public LabWork() {
        this.creationDate = new Date();
    }

    public LabWork(String name, Coordinates coordinates, String description, 
                   Difficulty difficulty, Discipline discipline, Integer minimalPoint, Person author) {
        this.name = name;
        this.coordinates = coordinates;
        this.description = description;
        this.difficulty = difficulty;
        this.discipline = discipline;
        this.minimalPoint = minimalPoint;
        this.author = author;
        this.creationDate = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Discipline getDiscipline() {
        return discipline;
    }

    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
    }

    public Integer getMinimalPoint() {
        return minimalPoint;
    }

    public void setMinimalPoint(Integer minimalPoint) {
        this.minimalPoint = minimalPoint;
    }

    public Person getAuthor() {
        return author;
    }

    public void setAuthor(Person author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "LabWork{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", description='" + description + '\'' +
                ", difficulty=" + difficulty +
                ", discipline=" + discipline +
                ", minimalPoint=" + minimalPoint +
                ", author=" + author +
                '}';
    }
}