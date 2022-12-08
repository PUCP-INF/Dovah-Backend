/*
 * Nombre del archivo: UsuarioRepository
 * Fecha de creación: 1/10/2022 , 09:05
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.RRHH.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.Reglas.model.Rol;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Integer> {
    @EntityGraph(attributePaths = {"listaRoles"})
    Optional<Usuario> queryAllByIdUsuario(int id);

    @EntityGraph(attributePaths = {"listaRoles"})
    Optional<Usuario> queryByCorreoAndActivoIsTrue(String correo);

    Optional<Usuario> findByCodigoPUCPContainingAndActivoIsTrue(String codigoPUCP);

    Optional<Usuario> queryByCodigoPUCPOrCorreo(String codigoPUCP, String correo);

    @Query("select distinct u from Usuario u left join fetch u.listaRoles where u.activo = true and u.correo = :correo")
    Optional<Usuario> queryByCorreoWithRoles(String correo);

    @Query("select distinct u from Usuario u left join fetch u.listaRoles where u.activo = true and u.idUsuario = :id")
    Optional<Usuario> queryByIdWithRoles(int id);

    @Query("select distinct u from Usuario u left join fetch u.listaRoles where u.activo = true")
    List<Usuario> queryAllWithRoles();

    @Query("select distinct u " +
            "from Usuario u " +
            "left join fetch u.listaRoles " +
            "where u.activo = true and u.especialidad.idEspecialidad = :idEspecialidad")
    List<Usuario> queryAllByEspecialidad(int idEspecialidad);

    @Query("select distinct u " +
            "from Usuario u " +
            "left join fetch u.listaRoles " +
            "where u.activo = true " +
            "and u.especialidad.idEspecialidad = :idEspecialidad " +
            "and size(u.listaRoles) > 0")
    List<Usuario> queryAllByEspecialidadRolesExist(int idEspecialidad);

    @Query("""
            select distinct u
            from Usuario u
            left join fetch u.listaRoles lr
            where u.activo = true
            and :rol in lr
""")
    List<Usuario> queryUsersWithRol(Rol rol);

    @Query("""
            select distinct u
            from Usuario u
            left join fetch u.listaRoles lr
            where u.activo = true
            and size(lr) = 1
            and :rol in lr
""")
    List<Usuario> queryUsersWithSingleRol(Rol rol);
}
