package pe.edu.pucp.dovah.PlanTesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.dovah.PlanTesis.model.AreaPlanTesis;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AreaPlanTesisRepository extends JpaRepository<AreaPlanTesis,Integer> {


    Set<AreaPlanTesis> queryAllByEstadoIsTrueAndEspecialidad_IdEspecialidad(int idEspecialidad);

    Optional<AreaPlanTesis> queryByIdAndEstadoIsTrue(int idAreaPlanTesis);
}
