/**
 * Nombre del archivo: Tarea.java
 * Fecha de creacion: 20/09/2022
 * Autor: Carlos Toro
 * Descripcion: Clase que implementa el repositorio (DAO) del modelo tarea
 */

package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.asignaciones.model.Tarea;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long>, CustomTareaRepository {
    Optional<Tarea> queryByIdAndActivoIsTrue(Long id);

    List<Tarea> queryAllByActivoIsTrue();

    @Query("select t from Tarea t left join fetch t.rolesEncargados where t.id = :id and t.activo = true")
    Optional<Tarea> queryByIdWithEncargados(@Param("id") Long id);

    @Query("select t from Tarea t where t.curso.idCurso = :idCurso and t.activo = true")
    List<Tarea> queryAllByIdCurso(int idCurso);

    @Query("""
select t
from Tarea t
left join fetch t.rolesEncargados re
left join fetch t.rubrica ru
left join fetch ru.criterios
where t.id = :id
and t.activo = true
""")
    Optional<Tarea> queryByTareaIdWithRubrica(Long id);

    @Query("select t from Tarea t left join fetch t.curso where t.visible = true and t.activo = true and t.curso.idCurso = :id")
    List<Tarea> queryAllByVisibleIsTrue(@Param("id") int id);

    @Query("""
            select distinct t
            from Tarea t
            left join fetch t.rolesEncargados re
            where t.activo = true
            and t.curso.idCurso = :idCurso
            and :rol in re
""")
    List<Tarea> queryAllTareasByRolEncargado(int idCurso, Rol rol);
    @Query("""
            select t
            from Tarea t
            left join fetch t.tareaEntregas
            where t.activo = true
            and t.fechaLimite < :fecha
""")
    List<Tarea> queryAllTareasByFechaFinalizada(LocalDateTime fecha);

    @Query("""
            select t
            from Tarea t
            left join fetch t.rolesEncargados
            where t.activo = true
            and t.id = :idTarea
""")
    Optional<Tarea> queryTareaWhitRoles(long idTarea);



}
