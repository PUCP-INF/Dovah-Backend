package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.mensajes.model.Hilo;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class TareaEntrega {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @JsonIgnoreProperties({"tareaEntregas", "planTesis", "curso"})
    @ManyToOne
    private Alumno alumno;

    @JsonIgnoreProperties("tareaEntregas")
    @ManyToOne
    private Tarea tarea;

    @ManyToMany
    @OrderBy(value = "fechaCreacion asc")
    private Set<Documento> listaDocumentos;

    private Float notaFinal = 0F;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @OneToMany(mappedBy = "tareaEntrega")
    @OrderBy(value = "fechaCreacion asc")
    private Set<Retroalimentacion> retroalimentaciones;

    @JsonIgnoreProperties("entregas")
    @ManyToOne
    private PlanTesis planTesis;

    @OneToOne
    private Hilo hilo;

    @OneToOne
    private Hilo avances;

    @JsonIgnoreProperties("entrega")
    @OneToOne
    private TareaAvances avancesOld;

    private Instant fechaCreacion = Instant.now();

    private Instant ultimaModificacion;

    private boolean activo = true;

    private EstadoEntrega estadoEntrega = EstadoEntrega.PENDIENTE;
    public TareaEntrega(Alumno alumno, Tarea tarea) {
        this.alumno = alumno;
        this.tarea = tarea;
    }
    private boolean vistoBueno = false;
}
