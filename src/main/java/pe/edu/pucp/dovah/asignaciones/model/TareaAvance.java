package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class TareaAvance {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @OneToMany
    private Set<Documento> documentos;

    @JsonIgnoreProperties("listaAvances")
    @ManyToOne
    private TareaAvances avance;
}
