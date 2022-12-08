package pe.edu.pucp.dovah.asignaciones.repository;

import pe.edu.pucp.dovah.asignaciones.model.Documento;

import java.util.List;

public interface CustomTareaRepository {
    List<Documento> getMaterialFromTarea(Long idTarea);
}
