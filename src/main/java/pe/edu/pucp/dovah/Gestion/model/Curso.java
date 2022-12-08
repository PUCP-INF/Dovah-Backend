/*
* Nombre del archivo: Curso.java
* Fecha de creacion: 20-09-2022
* Autor: Victor Avalos
* Descripción: Definición de la clase Curso
*/
package pe.edu.pucp.dovah.Gestion.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.asignaciones.model.Documento;
import pe.edu.pucp.dovah.asignaciones.model.Tarea;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    /*Clave, Nombre del Curso, Ciclo(Semestre?), Descripcion, Coordinador del Curso, Lista de Profesores, Lista de Alumnos */
    private int idCurso;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    private String clave;

    private String nombre;

    @CreationTimestamp
    private Instant fechaCreacion;

    private boolean activo = true;

    @JsonIgnoreProperties("cursos")
    @ManyToOne
    private Especialidad especialidad;

    @JsonIgnoreProperties("cursos")
    @ManyToOne
    private Semestre semestre;

    @JsonIgnoreProperties("curso")
    @OneToMany (mappedBy = "curso", cascade = CascadeType.ALL)
    private List<Tarea> tareas;

    @JsonIgnoreProperties("curso")
    @OneToMany(mappedBy = "curso")
    private Set<Alumno> alumnos;

    @JsonIgnoreProperties("curso")
    @OneToMany(mappedBy = "curso")
    private Set<Profesor> profesores;

    @ManyToMany
    private List<Documento> documentosGenerales;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="idCurso")
    @JsonIdentityReference(alwaysAsId=true)
    @OneToOne
    private Curso cursoRequisito;

    public Curso(String clave, String nombre, Especialidad especialidad, Semestre semestre) {
        this.clave = clave;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.semestre = semestre;
    }

    @Override
    public String toString() {
        return String.format("Curso(clave='%s', nombre='%s')", this.getClave(), this.getNombre());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Curso curso = (Curso) o;

        return uuid.equals(curso.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
