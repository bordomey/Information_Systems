package lab1.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "person")
public class Person implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String name; 
    
    @Enumerated(EnumType.STRING)
    private Color eyeColor; 
    
    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", nullable = false)
    private Color hairColor; 
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location; 
    
    @Column(nullable = false)
    private double height;  
    
    @Column(name = "passport_id", nullable = false)
    private String passportID; 


    public Person() {}

    public Person(String name, Color eyeColor, Color hairColor, Location location, double height, String passportID) {
        this.name = name;
        this.eyeColor = eyeColor;
        this.hairColor = hairColor;
        this.location = location;
        this.height = height;
        this.passportID = passportID;
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

    public Color getEyeColor() {
        return eyeColor;
    }

    public void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public Color getHairColor() {
        return hairColor;
    }

    public void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getPassportID() {
        return passportID;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", eyeColor=" + eyeColor +
                ", hairColor=" + hairColor +
                ", location=" + location +
                ", height=" + height +
                ", passportID='" + passportID + '\'' +
                '}';
    }
}