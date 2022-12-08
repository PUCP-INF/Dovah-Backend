package pe.edu.pucp.dovah.RRHH.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.PlanTesis.model.ProfesorInscritoPlanTesis;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.asignaciones.model.Retroalimentacion;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/*
 * Nombre del archivo: Profesor
 * Fecha de creación: 20/09/2022 , 18:00
 * Autor: Lloyd Castillo Ramos
 * Descripción: Clase profesor
 */
@Entity
@Getter @Setter @NoArgsConstructor
public class Profesor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @ManyToOne
    private Usuario usuario;

    @JsonIgnoreProperties("profesor")
    @ManyToOne
    private Curso curso;

    @JsonIgnoreProperties("listaUsuarios")
    @OrderBy(value = "fechaCreacion asc")
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Rol> roles = new HashSet<>();

    private boolean activo = true;

    @JsonIgnoreProperties("profesor")
    @OneToMany (mappedBy = "profesor", cascade = CascadeType.ALL)
    private List<Retroalimentacion> retroalimentaciones;

    @JsonIgnoreProperties("profesor")
    @OneToMany(mappedBy = "profesor")
    private Set<ProfesorInscritoPlanTesis> tesisInscritas;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profesor profesor = (Profesor) o;

        return uuid.equals(profesor.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
