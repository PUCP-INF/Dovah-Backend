/*
 * Nombre del archivo: PlanTesisRepository
 * Fecha de creación: 18/10/2022 , 12:00
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.PlanTesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.RRHH.model.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PlanTesisRepository extends JpaRepository<PlanTesis,Integer> {
    List<PlanTesis> queryByPeriodoPlanTesis_Id(Long id);

    List<PlanTesis> findByProponiente_IdUsuario(int id);

    @Query("select pt from PlanTesis pt where pt.estado=1 and pt.periodoPlanTesis.id =:idPeriodo")
    List<PlanTesis> queryByPeriodoAndEstadoAprobado(Long idPeriodo);

    @Query("""
            select pt from PlanTesis pt
            left join fetch pt.alumnos alu
            left join fetch pt.profesores profs
            left join fetch profs.profesor prof
            where pt.estado = 1
            and alu.usuario = :usuario or prof.usuario = :usuario
""")
    Set<PlanTesis> queryAllTesisWithUsuarioInscrito(Usuario usuario);

    @Query("""
            select pt from PlanTesis pt
            left join fetch pt.profesores profs
            left join fetch profs.profesor pro
            where pro = :profesor
            and pro.activo = true
""")
    Set<PlanTesis> queryAllTesisFromProfesor(Profesor profesor);

    @Query("select pt from PlanTesis pt left join fetch pt.alumnos where pt.id = :idPlanTesis")
    Optional<PlanTesis> queryByIdPlanTesisAndAlumnos(int idPlanTesis);

    @Query("select pt from PlanTesis pt left join fetch pt.profesores pr left join fetch pr.roles where pt.id = :idPlanTesis")
    Optional<PlanTesis> queryByIdPlanTesisAndProfesores(int idPlanTesis);
}
