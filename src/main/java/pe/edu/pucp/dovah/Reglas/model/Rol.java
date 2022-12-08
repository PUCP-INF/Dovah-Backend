package pe.edu.pucp.dovah.Reglas.model;/*
 * Nombre del archivo: Rol
 * Fecha de creación: 1/10/2022 , 07:06
 * Autor: Lloyd Castillo Ramos
 * Descripción:
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import pe.edu.pucp.dovah.RRHH.model.Usuario;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int idRol;

    private String nombre;

    @JsonIgnoreProperties("listaRoles")
    @ManyToMany(mappedBy = "listaRoles", cascade = CascadeType.ALL)
    private List<Usuario> listaUsuarios;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    @CreationTimestamp
    private Instant fechaCreacion;

    public Rol(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return String.format("Rol(id='%d', nombre='%s')", idRol, nombre);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rol rol = (Rol) o;

        return uuid.equals(rol.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
