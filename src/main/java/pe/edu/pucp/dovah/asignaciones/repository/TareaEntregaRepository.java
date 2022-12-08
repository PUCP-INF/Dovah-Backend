package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.asignaciones.model.TareaEntrega;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TareaEntregaRepository extends JpaRepository<TareaEntrega, Long> {
    @Query("""
            select distinct te
            from TareaEntrega te
            left join fetch te.listaDocumentos
            left join fetch te.retroalimentaciones
            left join fetch te.tarea ta
            left join fetch ta.rolesEncargados
            left join fetch ta.rubrica ru
            left join fetch ru.criterios
            where te.alumno.id = :idAlumno
            and te.tarea.id = :idTarea
""")
    Optional<TareaEntrega> queryTareaEntregaByAlumno_IdAndTarea_Id(Long idAlumno, Long idTarea);

    @Query("""
            select distinct te
            from TareaEntrega te
            left join fetch te.tarea ta
            where ta.id = :idTarea
            and te.planTesis in :planTesisList
            and (te.ultimaModificacion is not null or ta.esExposicion = true)
""")
    List<TareaEntrega> queryEntregasByTareaIdAndPlanTesisId(Long idTarea, Set<PlanTesis> planTesisList);

    @Query("""
                select te from TareaEntrega te left join fetch te.alumno al
                left join fetch te.avancesOld av left join fetch av.listaAvances lav where te.tarea.id = :idTarea and al.id = :idAlumno
    """)
    Optional<TareaEntrega> queryByTareaEntregaByAlumnoAndTareaAvances(Long idTarea,Long idAlumno);

    @Query("select te from TareaEntrega te left join fetch te.listaDocumentos where te.tarea.id =:idTarea")
    List<TareaEntrega> queryTareaEntregasByIdTarea(long idTarea);

    @Query("""
            select distinct te
            from TareaEntrega te
            left join fetch te.tarea ta
            where te.activo = true
            and te.alumno = :alumno
            order by ta.fechaLimite
""")
    Set<TareaEntrega> queryTareaEntregasByAlumno(Alumno alumno);

    @Query("""
            select distinct te
            from TareaEntrega te
            left join fetch te.listaDocumentos
            where te.activo = true
            and te.id = :idTareaEntrega
""")
    Optional<TareaEntrega> queryByIdWithDocumentos(Long idTareaEntrega);
}
