package lab1.rest.dtos;

import lab1.model.Difficulty;
import lab1.model.Color;

public class LabWorkDTO {
    private String name;
    private CoordinatesDTO coordinates;
    private String description;
    private Difficulty difficulty;
    private DisciplineDTO discipline;
    private Integer minimalPoint;
    private PersonDTO author;
    

    public LabWorkDTO() {}
    

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public CoordinatesDTO getCoordinates() {
        return coordinates;
    }
    
    public void setCoordinates(CoordinatesDTO coordinates) {
        this.coordinates = coordinates;
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
    
    public DisciplineDTO getDiscipline() {
        return discipline;
    }
    
    public void setDiscipline(DisciplineDTO discipline) {
        this.discipline = discipline;
    }
    
    public Integer getMinimalPoint() {
        return minimalPoint;
    }
    
    public void setMinimalPoint(Integer minimalPoint) {
        this.minimalPoint = minimalPoint;
    }
    
    public PersonDTO getAuthor() {
        return author;
    }
    
    public void setAuthor(PersonDTO author) {
        this.author = author;
    }
    
    public static class CoordinatesDTO {
        private int x;
        private int y;
        
        public CoordinatesDTO() {}
        
        public int getX() {
            return x;
        }
        
        public void setX(int x) {
            this.x = x;
        }
        
        public int getY() {
            return y;
        }
        
        public void setY(int y) {
            this.y = y;
        }
    }
    
    public static class DisciplineDTO {
        private String name;
        private int lectureHours;
        
        public DisciplineDTO() {}
        
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
    }
    
    public static class PersonDTO {
        private String name;
        private Color eyeColor;
        private Color hairColor;
        private LocationDTO location;
        private double height;
        private String passportID;
        
        public PersonDTO() {}
        
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
        
        public LocationDTO getLocation() {
            return location;
        }
        
        public void setLocation(LocationDTO location) {
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
    }
    
    public static class LocationDTO {
        private int x;
        private Integer y;
        private Long z;
        
        public LocationDTO() {}
        
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
    }
}