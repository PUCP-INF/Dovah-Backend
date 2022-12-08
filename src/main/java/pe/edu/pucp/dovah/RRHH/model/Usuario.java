package pe.edu.pucp.dovah.RRHH.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import pe.edu.pucp.dovah.Gestion.model.Especialidad;
import pe.edu.pucp.dovah.Gestion.model.Facultad;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.asignaciones.model.Documento;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
/*
 * Nombre del archivo: Usuario
 * Fecha de creación: 20/09/2022 , 18:00
 * Autor: Lloyd Castillo Ramos
 * Descripción: Clase usuario
 */

@Entity
@Getter @Setter @NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.PROTECTED)
    private int idUsuario;

    @NaturalId
    private UUID uuid = UUID.randomUUID();

    private String nombre;

    private String apellido;

    private char sexo;

    private String codigoPUCP;

    private String correo;

    private boolean activo = true;

    @JsonSerialize
    @Transient
    private String tipoLogin;

    private String picture;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @CreationTimestamp
    private Instant fechaCreacion;

    private Instant lastLogin;

    @JsonIgnoreProperties("usuario")
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Documento>listaDocumentos;

    @JsonIgnoreProperties({"listaUsuarios", "facultad", "cursos", "activo", "areasPlanTesis"})
    @ManyToOne
    Especialidad especialidad;

    @JsonIgnoreProperties({"listaUsuarios", "especialidades", "activo"})
    @ManyToOne
    Facultad facultad;

    @JsonIgnoreProperties("listaUsuarios")
    @OrderBy(value = "fechaCreacion asc")
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Rol> listaRoles = new HashSet<>();

    public Usuario(String nombre,
                   String apellido, String codigoPUCP,
                   String correo,String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.codigoPUCP = codigoPUCP;
        this.correo = correo;
        this.password = password;
    }

    public Usuario(String correo, String password, Rol rol) {
        this.correo = correo;
        this.password = password;
        this.listaRoles.add(rol);
    }

    public Usuario(String correo, String password, Facultad facultad, Especialidad especialidad) {
        this.correo = correo;
        this.password = password;
        this.facultad = facultad;
        this.especialidad = especialidad;
    }

    public Usuario(String correo, String password, Set<Rol> listaRoles, Facultad facultad, Especialidad especialidad) {
        this.correo = correo;
        this.password = password;
        this.listaRoles = listaRoles;
        this.facultad = facultad;
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return String.format("Usuario(id=%d,correo='%s')",
                             idUsuario, correo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Usuario usuario = (Usuario) o;

        return uuid.equals(usuario.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
