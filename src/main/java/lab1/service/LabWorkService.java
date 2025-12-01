package lab1.service;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lab1.dao.LabWorkDAO;
import lab1.model.LabWork;
import lab1.model.Coordinates;
import lab1.model.Difficulty;
import lab1.model.Discipline;
import lab1.model.Person;
import java.util.List;
import java.util.Arrays;

@Stateless
public class LabWorkService {
    
    @Inject
    private LabWorkDAO labWorkDAO;
    
    public LabWork createLabWork(String name, Coordinates coordinates, String description,
                                 Difficulty difficulty, Discipline discipline, 
                                 Integer minimalPoint, Person author) {

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (discipline == null) {
            throw new IllegalArgumentException("Discipline cannot be null");
        }
        if (minimalPoint == null || minimalPoint <= 0) {
            throw new IllegalArgumentException("Minimal point must be greater than 0");
        }

        LabWork labWork = new LabWork(name, coordinates, description, difficulty, discipline, minimalPoint, author);
        return labWorkDAO.create(labWork);
    }

    public LabWork getLabWorkById(int id) {
        return labWorkDAO.findById(id);
    }

    public LabWork updateLabWork(int id, String name, Coordinates coordinates, String description,
                                 Difficulty difficulty, Discipline discipline, 
                                 Integer minimalPoint, Person author) {
        LabWork labWork = getLabWorkById(id);
        if (labWork == null) {
            return null;
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (coordinates == null) {
            throw new IllegalArgumentException("Coordinates cannot be null");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        if (discipline == null) {
            throw new IllegalArgumentException("Discipline cannot be null");
        }
        if (minimalPoint == null || minimalPoint <= 0) {
            throw new IllegalArgumentException("Minimal point must be greater than 0");
        }

        labWork.setName(name);
        labWork.setCoordinates(coordinates);
        labWork.setDescription(description);
        labWork.setDifficulty(difficulty);
        labWork.setDiscipline(discipline);
        labWork.setMinimalPoint(minimalPoint);
        labWork.setAuthor(author);
        
        return labWorkDAO.update(labWork);
    }

    public boolean deleteLabWork(int id) {
        LabWork labWork = getLabWorkById(id);
        if (labWork == null) {
            return false;
        }
        labWorkDAO.delete(id);
        return true;
    }

    public List<LabWork> getAllLabWorks() {
        return labWorkDAO.findAll();
    }

    public double calculateAverageMinimalPoint() {
        return labWorkDAO.calculateAverageMinimalPoint();
    }

    public List<LabWork> filterByDescriptionSubstring(String substring) {
        return labWorkDAO.findByDescriptionSubstring(substring);
    }

    public void addTop10MostDifficultToDiscipline(Discipline discipline) {

        List<LabWork> allLabWorks = labWorkDAO.findAll();

        allLabWorks.sort((lw1, lw2) -> {
            if (lw1.getDifficulty() == null && lw2.getDifficulty() == null) return 0;
            if (lw1.getDifficulty() == null) return 1;  
            if (lw2.getDifficulty() == null) return -1; 
            return Integer.compare(lw2.getDifficulty().ordinal(), lw1.getDifficulty().ordinal());
        });
        
        int count = Math.min(10, allLabWorks.size());
        for (int i = 0; i < count; i++) {
            LabWork labWork = allLabWorks.get(i);
            labWork.setDiscipline(discipline);
            labWorkDAO.update(labWork);
        }
        
        System.out.println("Added " + count + " most difficult lab works to discipline: " + discipline.getName());
    }
    
    public List<LabWork> filterByAuthorGreaterThan(String authorName) {
        return labWorkDAO.findByAuthorGreaterThan(authorName);
    }
    
    public boolean decreaseDifficulty(int id, int steps) {
        LabWork labWork = getLabWorkById(id);
        if (labWork == null || labWork.getDifficulty() == null) {
            return false;
        }

        Difficulty[] difficulties = Difficulty.values();
        int currentIndex = Arrays.asList(difficulties).indexOf(labWork.getDifficulty());
        int newIndex = Math.max(0, currentIndex - steps);
        labWork.setDifficulty(difficulties[newIndex]);
        
        labWorkDAO.update(labWork);
        return true;
    }
}