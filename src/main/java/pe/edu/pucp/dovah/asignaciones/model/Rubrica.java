package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class Rubrica {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @JsonIgnoreProperties("rubrica")
    @OneToOne(mappedBy = "rubrica", cascade = CascadeType.ALL)
    private Tarea tarea;

    @JsonIgnoreProperties("rubrica")
    @OneToMany(mappedBy = "rubrica")
    @OrderBy(value = "fechaCreacion asc")
    private Set<RubricaCriterio> criterios = new HashSet<>();

    @JsonIgnoreProperties("rubrica")
    @OneToMany(mappedBy = "rubrica")
    @OrderBy(value = "fechaCreacion asc")
    private Set<Retroalimentacion> retroalimentaciones;

    private Float notaMaximaTotal = 0F;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rubrica rubrica = (Rubrica) o;

        return uuid.equals(rubrica.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
