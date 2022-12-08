package pe.edu.pucp.dovah.mensajes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class Hilo {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Type(type="org.hibernate.type.UUIDCharType")
    @NaturalId
    private UUID uuid = UUID.randomUUID();

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @JsonIgnoreProperties("hilo")
    @OrderBy(value = "fechaCreacion DESC")
    @OneToMany(mappedBy = "hilo")
    private Set<Comentario> comentarios = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hilo hilo = (Hilo) o;

        return uuid.equals(hilo.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
