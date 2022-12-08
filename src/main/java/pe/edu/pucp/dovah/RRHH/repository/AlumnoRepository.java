/*
 * Nombre del archivo: AlumnoRepository
 * Fecha de creación: 15/10/2022 , 09:15
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.RRHH.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
    List<Alumno> queryAllByActivoIsTrue();

    List<Alumno>findByUsuario_Especialidad_IdEspecialidad(int idEspecialidad);

    Optional<Alumno> queryByUsuarioAndCurso(Usuario usuario, Curso curso);

    Optional<Alumno> queryByIdAndCurso(Long idAlumno, Curso curso);

    Set<Alumno> queryByUsuarioAndActivoIsTrue(Usuario usuario);
    
    Set<Alumno> queryAllByCursoAndPlanTesis(Curso curso, PlanTesis planTesis);

    Set<Alumno> queryAllByCursoAndPlanTesisIsNull(Curso curso);

    Set<Alumno> queryAllByCursoAndPlanTesisIsNotNull(Curso curso);

    @Query("""
            select distinct a
            from Alumno a
            where a.activo = true
            and a.curso = :curso
""")
    Optional<Alumno> queryByCursoWithTesis(Curso curso);
}
