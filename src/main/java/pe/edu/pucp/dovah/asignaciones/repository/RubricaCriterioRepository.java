package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.dovah.asignaciones.model.RubricaCriterio;

import java.util.List;

public interface RubricaCriterioRepository extends JpaRepository<RubricaCriterio, Long> {

    List<RubricaCriterio> queryAllByRubricaId(Long idRubrica);
}
