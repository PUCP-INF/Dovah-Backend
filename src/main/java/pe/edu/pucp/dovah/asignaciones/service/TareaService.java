package pe.edu.pucp.dovah.asignaciones.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;
import pe.edu.pucp.dovah.asignaciones.model.*;
import pe.edu.pucp.dovah.asignaciones.repository.*;
import pe.edu.pucp.dovah.mensajes.model.Hilo;
import pe.edu.pucp.dovah.mensajes.repository.HiloRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class TareaService {
    private final RubricaRepository rubricaRepository;
    private final RubricaCriterioRepository rubricaCriterioRepository;
    private final RolRepository rolRepository;
    private final TareaEntregaRepository tareaEntregaRepository;
    private final RetroalimentacionRepository retroalimentacionRepository;
    private final TareaRepository tareaRepository;
    private final HiloRepository hiloRepository;

    public TareaService(RubricaRepository rubricaRepository,
                        RubricaCriterioRepository rubricaCriterioRepository,
                        TareaEntregaRepository tareaEntregaRepository,
                        RolRepository rolRepository,HiloRepository hiloRepository,
                        RetroalimentacionRepository retroalimentacionRepository,TareaRepository tareaRepository) {
        this.rubricaRepository = rubricaRepository;
        this.rubricaCriterioRepository = rubricaCriterioRepository;
        this.rolRepository = rolRepository;
        this.tareaEntregaRepository = tareaEntregaRepository;
        this.hiloRepository = hiloRepository;
        this.retroalimentacionRepository = retroalimentacionRepository;
        this.tareaRepository = tareaRepository;
    }

    public void asignarRoles(Tarea tarea, JSONArray array) {
        Set<Rol> tmp = new HashSet<>();
        for (Object obj: array) {
            var rol = rolRepository.queryByNombre(obj.toString()).orElseThrow();
            tmp.add(rol);
        }
        tarea.setRolesEncargados(tmp);
    }

    public TareaEntrega crearEntregaVacia(Tarea tarea, Alumno alumno) {
        var tareaEntrega = new TareaEntrega();
        tareaEntrega.setHilo(new Hilo());
        hiloRepository.save(tareaEntrega.getHilo());
        Hilo avances = new Hilo();
        hiloRepository.save(avances);
        tareaEntrega.setAvances(avances);
        tareaEntrega.setTarea(tarea);
        tareaEntrega.setPlanTesis(alumno.getPlanTesis());
        tareaEntrega.setAlumno(alumno);
        tareaEntrega.setListaDocumentos(new HashSet<>());
        return tareaEntregaRepository.save(tareaEntrega);
    }

    public void asignarRubrica(Tarea tarea, JSONObject json) {
        Rubrica rubrica = new Rubrica();
        var id = json.getLong("id");
        if (id != 0) rubrica = rubricaRepository.queryById(id);
        rubrica.setNotaMaximaTotal(json.getFloat("notaMaximaTotal"));
        rubricaRepository.save(rubrica);
        // varios bloques en un ciclo
        for (Object obj: json.getJSONArray("criterios")) {
            var critJson = new JSONObject(obj.toString());
            long critId = critJson.getLong("id");
            RubricaCriterio criterio;
            if (critId == 0) {
                // nuevo criterio
                criterio = new RubricaCriterio();
                criterio.setRubrica(rubrica);
            } else {
                // criterio existente
                criterio = rubricaCriterioRepository.findById(critId).orElseThrow();
            }
            criterio.setDescripcion(critJson.getString("descripcion"));
            criterio.setTitulo(critJson.getString("titulo"));
            criterio.setNotaMaxima(critJson.getFloat("notaMaxima"));
            rubricaCriterioRepository.save(criterio);
            if (!rubrica.getCriterios().add(criterio)) {
                rubrica.getCriterios().remove(criterio);
                rubrica.getCriterios().add(criterio);
            }
        }
        rubricaRepository.save(rubrica);
        tarea.setRubrica(rubrica);
    }

    public void actualizarNota(TareaEntrega tareaEntrega){
        var retroalimentaciones = retroalimentacionRepository.queryByTareaEntrega(tareaEntrega);
        float notaFinal = 0;
        for (var retro: retroalimentaciones) {
            for (var notas: retro.getNotasObtenidas().values()) {
                notaFinal += notas;
            }
        }
        tareaEntrega.setNotaFinal((float)Math.round(notaFinal / retroalimentaciones.size()));
        tareaEntregaRepository.save(tareaEntrega);
    }

    public void verificarVistoBueno(Tarea tarea){
        var rol = rolRepository.queryByNombre("ASESOR").orElseThrow();
        tarea.setNecesitaVistoBueno(tarea.getRolesEncargados().size() > 1 && tarea.getRolesEncargados().contains(rol));
    }
}
