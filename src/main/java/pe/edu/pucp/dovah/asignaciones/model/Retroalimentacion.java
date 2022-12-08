package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import pe.edu.pucp.dovah.RRHH.model.Profesor;

import javax.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Retroalimentacion {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private boolean activo = true;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    private TareaEntrega tareaEntrega;

    @JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
    @JsonIdentityReference(alwaysAsId=true)
    @ManyToOne
    private Rubrica rubrica;

    @ElementCollection
    private Map<Long, Float> notasObtenidas = new HashMap<>();

    @JsonIgnoreProperties("usuario")
    @ManyToMany
    private Set<Documento> listaDocumentos = new HashSet<>();

    @JsonIgnoreProperties("retroalimentaciones")
    @ManyToOne
    private Profesor profesor;

    private Instant fechaCreacion = Instant.now();

    private Float notaFinal = 0F;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Retroalimentacion that = (Retroalimentacion) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
