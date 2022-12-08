package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.dovah.asignaciones.model.Rubrica;

@Repository
public interface RubricaRepository extends JpaRepository<Rubrica, Long> {
    @EntityGraph(attributePaths = "criterios")
    Rubrica queryById(Long id);
}
