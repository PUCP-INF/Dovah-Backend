/**
 * Nombre del archivo: Inf245BackendApplication.java
 * Fecha de creacion:  19/09/2022
 * Autor: Carlos Toro
 * Descripcion: Clase principal para ejecutar el backend
 */

package pe.edu.pucp.dovah;

import org.jetbrains.annotations.Async;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class Inf245BackendApplication extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Inf245BackendApplication.class);
    }

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(Inf245BackendApplication.class, args);
    }
}
