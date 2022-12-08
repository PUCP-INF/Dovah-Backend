/**
 * Nombre del archivo: Tarea.java
 * Fecha de creacion: 20/09/2022
 * Autor: Carlos Toro
 * Descripcion: Clase que implementa el modelo de la base de datos
 */

package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.Reglas.model.Rol;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private Long id;
    private String descripcion;
    private String nombre;
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private LocalDateTime fechaLimite;
    private boolean activo = true;
    private boolean visible = false;
    private boolean esExposicion = false;
    private boolean necesitaVistoBueno = false;
    @JsonIgnoreProperties("tarea")
    @OneToOne
    private Rubrica rubrica;

    @JsonIgnoreProperties("tarea")
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL)
    private List<TareaEntrega> tareaEntregas;

    @JsonIgnoreProperties("listaUsuarios")
    @ManyToMany
    private Set<Rol> rolesEncargados = new HashSet<>();

    @JsonIncludeProperties("id")
    @OneToOne
    private Tarea tareaPadre;

    @JsonIgnoreProperties({"tareas", "especialidad"})
    @ManyToOne
    private Curso curso;

    private float peso=0;

    public Tarea(String descripcion) {
        this.descripcion = descripcion;
    }

    public Tarea(String nombre, String descripcion, LocalDateTime fechaLimite) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
    }

    @Override
    public String toString() {
        return String.format("Tarea(descripcion='%s')", this.getDescripcion());
    }
}
