package lab1;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import lab1.rest.LabWorkResource;
import lab1.rest.ImportResource;
import lab1.filter.CorsFilter;
import lab1.filter.AuthFilter;
import lab1.exception.GlobalExceptionHandler;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataReader;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataWriter;

@ApplicationPath("/api")
public class LabWorkApplication extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(LabWorkResource.class);
        classes.add(ImportResource.class);
        classes.add(CorsFilter.class);
        classes.add(AuthFilter.class);
        classes.add(GlobalExceptionHandler.class);
        
        classes.add(MultipartFormDataReader.class);
        classes.add(MultipartFormDataWriter.class);
        
        return classes;
    }
}