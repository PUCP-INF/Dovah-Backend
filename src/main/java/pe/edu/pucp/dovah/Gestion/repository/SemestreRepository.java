/*
 * Nombre del archivo: SemestreRepository.java
 * Fecha de creacion: 20-09-2022
 * Autor: Victor Avalos
 * Descripción: Interfaz de repositorio para la clase Semestre
 */
package pe.edu.pucp.dovah.Gestion.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.Gestion.model.Semestre;

import java.util.List;
import java.util.Optional;

public interface SemestreRepository extends JpaRepository<Semestre,Integer>{

    List<Semestre> queryAllByActivoIsTrue();
    @EntityGraph(attributePaths = {"cursos"})
    Optional<Semestre> findByIdSemestreAndActivoIsTrue(int idSemestre);

    @Query("""
            select distinct se
            from Semestre se
            where se.activo = true
            order by se.anhoAcademico asc, se.periodo asc
""")
    List<Semestre> queryAllActivos();
}
