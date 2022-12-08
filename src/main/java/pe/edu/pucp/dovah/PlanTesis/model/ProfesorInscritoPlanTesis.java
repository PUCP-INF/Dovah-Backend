package pe.edu.pucp.dovah.PlanTesis.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.Reglas.model.Rol;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class ProfesorInscritoPlanTesis {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    private Boolean activo = true;

    @JsonIgnoreProperties("tesisInscritas")
    @ManyToOne
    private Profesor profesor;

    @JsonIgnoreProperties("profesores")
    @ManyToOne
    private PlanTesis planTesis;

    @JsonIgnoreProperties("listaUsuarios")
    @ManyToMany
    private Set<Rol> roles = new HashSet<>();

    public ProfesorInscritoPlanTesis(Profesor profesor, Set<Rol> roles, PlanTesis planTesis) {
        this.profesor = profesor;
        this.roles = roles;
        this.planTesis = planTesis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProfesorInscritoPlanTesis that = (ProfesorInscritoPlanTesis) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
