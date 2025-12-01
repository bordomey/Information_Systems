package lab1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "discipline")
public class Discipline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String name; 
    
    @Column(name = "lecture_hours")
    private int lectureHours;

    public Discipline() {}

    public Discipline(String name, int lectureHours) {
        this.name = name;
        this.lectureHours = lectureHours;
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

    public int getLectureHours() {
        return lectureHours;
    }

    public void setLectureHours(int lectureHours) {
        this.lectureHours = lectureHours;
    }

    @Override
    public String toString() {
        return "Discipline{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lectureHours=" + lectureHours +
                '}';
    }
}