/*
 * Nombre del archivo: RetroalimentacionRepository
 * Fecha de creación: 7/11/2022 , 09:16
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.asignaciones.model.Retroalimentacion;
import pe.edu.pucp.dovah.asignaciones.model.Rubrica;
import pe.edu.pucp.dovah.asignaciones.model.TareaEntrega;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RetroalimentacionRepository extends JpaRepository<Retroalimentacion,Long> {

    @EntityGraph(attributePaths = {"listaDocumentos"})
    @Query("select distinct r from Retroalimentacion r" +
            " where r.tareaEntrega.id =:id and r.activo=true ")
    List<Retroalimentacion> queryAllWithDocumentsByTareaEntrega_Id(Long id);

    @Query("""
            select distinct r
            from Retroalimentacion r
            left join fetch r.notasObtenidas
            where r.rubrica = :rubrica
""")
    Set<Retroalimentacion> queryByRubrica(Rubrica rubrica);

    @Query("""
            select distinct r
            from Retroalimentacion r
            left join fetch r.notasObtenidas
            where r.tareaEntrega = :tareaEntrega
""")
    Set<Retroalimentacion> queryByTareaEntrega(TareaEntrega tareaEntrega);

    @Query("""
            select r
            from Retroalimentacion r
            left join fetch r.notasObtenidas
            left join fetch r.listaDocumentos
            where r.id = :id
""")
    Optional<Retroalimentacion> queryByIdFetchNotasAndDocumentos(Long id);

    @Query("""
            select distinct r
            from Retroalimentacion r
            left join fetch r.notasObtenidas
            left join fetch r.listaDocumentos
            left join fetch r.rubrica ru
            left join fetch ru.criterios cri
            where r.tareaEntrega = :tareaEntrega
""")
    Set<Retroalimentacion> queryByTareaEntregaWithNotasAndDocumentos(TareaEntrega tareaEntrega);
}
