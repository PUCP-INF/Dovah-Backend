package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.stereotype.Repository;
import pe.edu.pucp.dovah.asignaciones.model.Documento;
import pe.edu.pucp.dovah.asignaciones.model.Tarea;

import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomTareaRepositoryImpl implements CustomTareaRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Documento> getMaterialFromTarea(Long idTarea) {
        Query q1 = em.createNativeQuery("" +
                "select tm.material_id " +
                "from tarea_material tm " +
                "where tm.tarea_id = :id");
        q1.setParameter("id", idTarea);
        var res = new ArrayList<Long>();
        for (Object obj: q1.getResultList()) res.add(Long.valueOf(obj.toString()));
        TypedQuery<Documento> q2 = em.createQuery("select d " +
                "from Documento d " +
                "where d.id in :ids and d.activo = true", Documento.class);
        q2.setParameter("ids", res);
        return q2.getResultList();
    }
}
