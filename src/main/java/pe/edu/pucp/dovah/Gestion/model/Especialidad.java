/*
 * Nombre del archivo: Especialidad.java
 * Fecha de creacion: 20-09-2022
 * Autor: Victor Avalos
 * Descripción: Definición de la clase Especialidad
 */
package pe.edu.pucp.dovah.Gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.dovah.PlanTesis.model.AreaPlanTesis;
import pe.edu.pucp.dovah.RRHH.model.Usuario;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class Especialidad {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private int idEspecialidad;
    private String nombre;
    private String codigo;

    private boolean activo = true;

    private boolean alumnosProponenTesis = true;

    @JsonIgnoreProperties("especialidades")
    @ManyToOne
    private Facultad facultad;

    @JsonIgnoreProperties("especialidad")
    @OneToMany (mappedBy = "especialidad", cascade = CascadeType.ALL)
    private List<Usuario> listaUsuarios;

    @JsonIgnoreProperties("especialidad")
    @OneToMany (mappedBy = "especialidad", cascade = CascadeType.ALL)
    private List<Curso> cursos;

    @JsonIgnoreProperties("especialidad")
    @OneToMany(mappedBy = "especialidad",cascade = CascadeType.ALL)
    private Set<AreaPlanTesis> areasPlanTesis;

    public Especialidad(String nombre, String codigo, Facultad facultad) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.facultad = facultad;
    }

    public Especialidad(String nombre, String codigo) {
        this.nombre = nombre;
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return String.format("Especialidad(nombre='%s' codigo='%s')",
                this.getNombre(), this.getCodigo());
    }
}
