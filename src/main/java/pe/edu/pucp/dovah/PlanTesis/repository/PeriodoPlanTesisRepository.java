package pe.edu.pucp.dovah.PlanTesis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.PlanTesis.model.PeriodoPlanTesis;


public interface PeriodoPlanTesisRepository extends JpaRepository<PeriodoPlanTesis, Long> {
    @Query("select distinct p from PeriodoPlanTesis p where p.activo = true")
    PeriodoPlanTesis queryPeriodoActivo();
}
