package pe.edu.pucp.dovah.PlanTesis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pe.edu.pucp.dovah.Gestion.model.Especialidad;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class AreaPlanTesis {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private int id;

    private String nombre;

    @JsonIgnoreProperties("areaEspecialidad")
    @OneToMany(mappedBy = "areaEspecialidad", cascade = CascadeType.ALL)
    private List<PlanTesis> planTesis;

    @CreationTimestamp
    private Instant fechaCreacion;

    private boolean estado = true;

    @JsonIgnoreProperties("areasPlanTesis")
    @ManyToOne
    private Especialidad especialidad;

    public AreaPlanTesis(String nombre,Especialidad especialidad) {
        this.especialidad = especialidad;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "AreaPlanTesis{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}
