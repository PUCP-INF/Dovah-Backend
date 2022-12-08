package pe.edu.pucp.dovah.asignaciones.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import pe.edu.pucp.dovah.RRHH.model.Usuario;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(value = { "blobDoc", "uuid", "activo" })
@Entity
@Getter @Setter @NoArgsConstructor
public class Documento {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @JsonIgnoreProperties("listaDocumentos")
    @ManyToOne
    private Usuario usuario;

    private boolean activo = true;

    @Type(type="text")
    private String nombre;

    @Type(type="text")
    private String url;

    @Type(type="org.hibernate.type.UUIDCharType")
    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @Type(type="text")
    private String mediaType;

    private Instant fechaCreacion = Instant.now();

    public Documento(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return String.format("Documento(Nombre='%s')", this.getNombre());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Documento documento = (Documento) o;

        return uuid.equals(documento.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
