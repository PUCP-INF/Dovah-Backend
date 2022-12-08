package pe.edu.pucp.dovah.mensajes.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.asignaciones.model.Documento;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class Comentario {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @ManyToOne
    private Documento documento;

    @Type(type="text")
    private String mensaje;

    @OneToOne
    private Usuario usuario;

    @JsonIgnoreProperties("comentarios")
    @ManyToOne
    private Hilo hilo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comentario that = (Comentario) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
