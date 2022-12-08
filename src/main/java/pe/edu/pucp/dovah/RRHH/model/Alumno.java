package pe.edu.pucp.dovah.RRHH.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.asignaciones.model.TareaEntrega;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

/*
 * Nombre del archivo: Alumno
 * Fecha de creación: 20/09/2022 , 18:00
 * Autor: Lloyd Castillo Ramos
 * Descripción: Clase alumno
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @JsonIgnore
    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    private Usuario usuario;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="idCurso")
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    private Curso curso;

    @JsonIgnore
    private boolean activo = true;

    @JsonIgnoreProperties("alumnos")
    @ManyToOne
    private PlanTesis planTesis;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @OneToMany (mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<TareaEntrega> tareaEntregas;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alumno alumno = (Alumno) o;

        return uuid.equals(alumno.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
