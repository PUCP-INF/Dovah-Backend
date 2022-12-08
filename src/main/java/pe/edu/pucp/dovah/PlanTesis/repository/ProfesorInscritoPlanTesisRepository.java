package pe.edu.pucp.dovah.PlanTesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.PlanTesis.model.ProfesorInscritoPlanTesis;
import pe.edu.pucp.dovah.RRHH.model.Profesor;

import java.util.Optional;
import java.util.Set;

public interface ProfesorInscritoPlanTesisRepository extends JpaRepository<ProfesorInscritoPlanTesis, Long> {
    Optional<ProfesorInscritoPlanTesis> queryByProfesorAndPlanTesis(Profesor profesor, PlanTesis planTesis);

    @Query("""
            select distinct pi
            from ProfesorInscritoPlanTesis pi
            left join fetch pi.profesor prof
            left join fetch pi.roles ro
            where pi.planTesis = :planTesis
            and prof.curso = :curso
            and pi.activo = true
""")
    Set<ProfesorInscritoPlanTesis> queryAllByPlanTesisAndCurso(PlanTesis planTesis, Curso curso);

    @Query("""
            select distinct pi
            from ProfesorInscritoPlanTesis pi
            left join fetch pi.profesor prof
            left join fetch pi.roles ro
            where pi.planTesis = :planTesis
            and prof.curso = :curso
            and pi.activo = true
            and ro.nombre LIKE '%ASESOR%'
""")
    Set<ProfesorInscritoPlanTesis> queryAllByPlanTesisAndCursoWhitRol(PlanTesis planTesis, Curso curso);

    @Query("""
            select distinct pi
            from ProfesorInscritoPlanTesis pi
            left join fetch pi.profesor prof
            left join fetch pi.roles ro
            where prof.curso = :curso
            and pi.activo = true
""")
    Set<ProfesorInscritoPlanTesis> queryByCursoWithRoles(Curso curso);
}
