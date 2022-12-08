package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.dovah.mensajes.model.Hilo;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class TareaAvances {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @JsonIgnoreProperties("avance")
    @OneToMany(mappedBy = "avance")
    private Set<TareaAvance> listaAvances;

    @JsonIgnoreProperties("avances")
    @OneToOne
    private TareaEntrega entrega;

    @OneToOne
    private Hilo hilo;
}
