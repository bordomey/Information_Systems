package lab1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private int x;
    
    @Column(nullable = false)
    private Integer y; 
    
    @Column(nullable = false)
    private Long z; 


    public Location() {}

    public Location(int x, Integer y, Long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Long getZ() {
        return z;
    }

    public void setZ(Long z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}