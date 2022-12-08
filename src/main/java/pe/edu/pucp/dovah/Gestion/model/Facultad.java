/*
 * Nombre del archivo: Facultad.java
 * Fecha de creacion: 20-09-2022
 * Autor: Victor Avalos
 * Descripción: Definición de la clase Facultad
 */
package pe.edu.pucp.dovah.Gestion.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.pucp.dovah.RRHH.model.Usuario;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Facultad {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private int idFacultad;
    private String nombre;

    private boolean activo = true;

    @JsonIgnoreProperties("facultad")
    @OneToMany (mappedBy = "facultad", cascade = CascadeType.ALL)
    private List<Especialidad> especialidades;

    @JsonIgnoreProperties("facultad")
    @OneToMany (mappedBy = "facultad", cascade = CascadeType.ALL)
    private List<Usuario> listaUsuarios;

    public Facultad(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return String.format("Facultad(nombre='%s')", this.getNombre());
    }
}
