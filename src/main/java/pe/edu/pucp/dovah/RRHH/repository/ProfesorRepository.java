package pe.edu.pucp.dovah.RRHH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.Reglas.model.Rol;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
 * Nombre del archivo: ProfesorRepository
 * Fecha de creación: 20/09/2022 , 18:00
 * Autor: Lloyd Castillo Ramos
 * Descripción: Interfaz encargada de manejar los metodos trabajados sobre la entidad Profesor
 */
@Repository
public interface ProfesorRepository extends JpaRepository<Profesor,Long> {
    List<Profesor>queryAllByActivoIsTrue();

    Optional<Profesor> queryByUsuarioAndCurso(Usuario usuario, Curso curso);

    Optional<Profesor> queryByIdAndCurso(Long idProfesor, Curso curso);

    @Query("""
            select distinct p
            from Profesor p
            left join fetch p.tesisInscritas ti
            left join fetch ti.planTesis pt
            left join fetch ti.roles
            where p.activo = true
            and p.curso = :curso
            and (size(ti) = 0 or pt <> :planTesis)
""")
    Set<Profesor> queryAllInCursoNotInPlanTesis(Curso curso, PlanTesis planTesis);

    @Query("""
            select distinct p
            from Profesor p
            left join fetch p.usuario u
            left join fetch p.roles
            where p.activo = true
            and p.usuario = :usuario
""")
    Set<Profesor> queryAllByUsuarioWithRoles(Usuario usuario);

    @Query("""
            select distinct p
            from Profesor p
            left join fetch p.usuario u
            left join fetch p.roles ro
            where p.activo = true
            and p.usuario = :usuario
            and :rol in ro
""")
    Set<Profesor> queryAllByUsuarioWithRol(Usuario usuario, Rol rol);

    @Query("""
            select distinct p
            from Profesor p
            left join fetch p.curso cu
            where cu.especialidad.idEspecialidad = :idEspecialidad
            and p.activo = true
            and p.usuario.idUsuario not in
            (select p2.usuario.idUsuario from Profesor p2 left join p2.curso cu2 where p2.activo = true and cu2.idCurso = :idCurso)
""")
    List<Profesor> queryAllByCursoIsNull(int idCurso,int idEspecialidad);
}