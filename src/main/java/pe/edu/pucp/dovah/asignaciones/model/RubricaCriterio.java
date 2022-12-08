package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class RubricaCriterio {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @JsonIgnoreProperties("criterios")
    @ManyToOne
    private Rubrica rubrica;

    private String titulo;

    @Type(type="text")
    private String descripcion;

    private float notaMaxima;

    @CreationTimestamp
    private Instant fechaCreacion;

    public RubricaCriterio(String titulo, String descripcion, float notaMaxima) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.notaMaxima = notaMaxima;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RubricaCriterio that = (RubricaCriterio) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
