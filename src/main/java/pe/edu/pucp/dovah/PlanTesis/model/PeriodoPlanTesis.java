package pe.edu.pucp.dovah.PlanTesis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class PeriodoPlanTesis {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private boolean activo = true;

    @JsonIgnoreProperties("periodoPlanTesis")
    @OneToMany(mappedBy = "periodoPlanTesis")
    private List<PlanTesis> listaTesis;
}
