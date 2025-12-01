package lab1;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import lab1.rest.LabWorkResource;
import lab1.filter.CorsFilter;

@ApplicationPath("/api")
public class LabWorkApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(LabWorkResource.class);
        classes.add(CorsFilter.class);
        return classes;
    }
}