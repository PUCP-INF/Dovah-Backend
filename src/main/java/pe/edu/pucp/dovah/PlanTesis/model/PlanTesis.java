/*
 * Nombre del archivo: PlanTesis
 * Fecha de creación: 18/10/2022 , 11:51
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.PlanTesis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import pe.edu.pucp.dovah.Gestion.model.Facultad;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.asignaciones.model.TareaEntrega;
import pe.edu.pucp.dovah.mensajes.model.Hilo;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class PlanTesis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private int id;

    private String titulo;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @Type(type="text")
    private String descripcion;

    @Type(type="text")
    private String detallesAdicionales;

    @JsonIgnoreProperties("planTesis")
    @ManyToOne
    private AreaPlanTesis areaEspecialidad;

    private EstadoPlanTesis estado = EstadoPlanTesis.PENDIENTE;

    private Instant fechaCreacion = Instant.now();

    @JsonIgnoreProperties("planTesis")
    @OneToMany(mappedBy = "planTesis", cascade = CascadeType.ALL)
    private List<TareaEntrega> entregas;

    @ManyToOne
    private Usuario proponiente;

    @JsonIgnoreProperties("planTesis")
    @OneToMany(mappedBy = "planTesis")
    private Set<Alumno> alumnos = new HashSet<>();

    @JsonIgnoreProperties("planTesis")
    @OneToMany(mappedBy = "planTesis")
    private Set<ProfesorInscritoPlanTesis> profesores = new HashSet<>();

    @JsonIgnoreProperties("listaTesis")
    @ManyToOne
    private PeriodoPlanTesis periodoPlanTesis;

    @JsonIgnoreProperties({"listaUsuarios", "especialidades"})
    @OneToOne
    private Facultad facultad;

    @OneToOne
    private Hilo hilo;

    private EstadoTesis estadoTesis = EstadoTesis.EN_PROCESO;

    public PlanTesis(String titulo, AreaPlanTesis areaEspecialidad, String descripcion) {
        this.titulo = titulo;
        this.areaEspecialidad = areaEspecialidad;
        this.descripcion = descripcion;
    }

    public PlanTesis(String titulo, AreaPlanTesis areaEspecialidad, String descripcion, Facultad facultad) {
        this.titulo = titulo;
        this.areaEspecialidad = areaEspecialidad;
        this.descripcion = descripcion;
        this.facultad = facultad;
    }

    @Override
    public String toString() {
        return String.format("Tema de tesis(Titulo='%s')", this.getTitulo());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanTesis planTesis = (PlanTesis) o;

        return uuid.equals(planTesis.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
