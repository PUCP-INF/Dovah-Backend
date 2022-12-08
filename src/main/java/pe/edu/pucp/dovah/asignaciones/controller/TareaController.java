/**
 * Nombre del archivo: TareaController.java
 * Fecha de creacion: 20/09/2022
 * Autor: Carlos Toro
 * Descripcion: Clase que implementa la api de tareas
 */

package pe.edu.pucp.dovah.asignaciones.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.PlanTesis.repository.PlanTesisRepository;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.repository.AlumnoRepository;
import pe.edu.pucp.dovah.RRHH.repository.ProfesorRepository;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;
import pe.edu.pucp.dovah.asignaciones.exception.TareaNotFoundException;
import pe.edu.pucp.dovah.asignaciones.model.*;
import pe.edu.pucp.dovah.asignaciones.repository.*;
import pe.edu.pucp.dovah.asignaciones.service.TareaService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequestMapping("/api/v1/")
@RestController
public class TareaController {
    private final TareaRepository tareaRepository;
    private final DocumentoRepository documentoRepository;
    private final ProfesorRepository profesorRepository;
    private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final RetroalimentacionRepository retroalimentacionRepository;
    private final TareaEntregaRepository tareaEntregaRepository;
    private final RubricaCriterioRepository rubricaCriterioRepository;
    private final RolRepository rolRepository;
    private final TareaService tareaService;
    private final PlanTesisRepository planTesisRepository;
    private final TareaAvancesRepository tareaAvancesRepository;
    private final TareaAvanceRepository tareaAvanceRepository;
    private final RubricaRepository rubricaRepository;
    private final static Logger log = LoggerFactory.getLogger(TareaController.class);

    public TareaController(TareaRepository tareaRepository,
                           DocumentoRepository documentoRepository,
                           ProfesorRepository profesorRepository,
                           CursoRepository cursoRepository,
                           TareaService tareaService,
                           AlumnoRepository alumnoRepository, TareaEntregaRepository tareaEntregaRepository,
                           RubricaCriterioRepository rubricaCriterioRepository,
                           RolRepository rolRepository,
                           PlanTesisRepository planTesisRepository,
                           RetroalimentacionRepository retroalimentacionRepository,
                           RubricaRepository rubricaRepository,
                           TareaAvancesRepository tareaAvancesRepository,TareaAvanceRepository tareaAvanceRepository) {
        this.tareaRepository = tareaRepository;
        this.documentoRepository = documentoRepository;
        this.profesorRepository = profesorRepository;
        this.cursoRepository = cursoRepository;
        this.tareaService = tareaService;
        this.alumnoRepository = alumnoRepository;
        this.tareaEntregaRepository = tareaEntregaRepository;
        this.rubricaCriterioRepository = rubricaCriterioRepository;
        this.retroalimentacionRepository = retroalimentacionRepository;
        this.planTesisRepository = planTesisRepository;
        this.rolRepository = rolRepository;
        this.tareaAvancesRepository = tareaAvancesRepository;
        this.tareaAvanceRepository = tareaAvanceRepository;
        this.rubricaRepository = rubricaRepository;
    }

    /**
     * Listar todas las tareas registradas
     * @return arreglo con todas las tareas
     */
    @GetMapping("/tareas")
    List<Tarea> all() {
        return tareaRepository.queryAllByActivoIsTrue();
    }

    /**
     * Devolver una tarea en particular
     * @param id id de una tarea
     * @return detalles de una tarea
     */
    @GetMapping("/tareas/{id}")
    Tarea one(@PathVariable Long id) {
        return tareaRepository.queryByIdAndActivoIsTrue(id).orElseThrow(() -> new TareaNotFoundException(id));
    }

    @GetMapping("/tareas/tareasVisibles/{id}")
    List<Tarea> listarTareasVisibles(@PathVariable int id){
        return tareaRepository.queryAllByVisibleIsTrue(id);
    }

    @GetMapping("/tareas/with_rubrica/{id}")
    Tarea oneWithRubrica(@PathVariable Long id) {
        return tareaRepository.queryByTareaIdWithRubrica(id).orElseThrow();
    }

    /**
     * Cambiar visibildad de una tarea
     * @param map json con la siguiente estructura
     *            {
     *              "idTarea": int,
     *              "visible": bool
     *            }
     * @return tarea modificada
     */
    @PostMapping("/tareas/cambiarVisibilidad")
    Tarea cambiarVisibilidad(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var tarea = tareaRepository.queryByIdAndActivoIsTrue(json.getLong("idTarea")).orElseThrow();
        tarea.setVisible(json.getBoolean("visible"));
        return tareaRepository.save(tarea);
    }

    @GetMapping("/tareas/por_curso/{idCurso}")
    List<Tarea> getTareasFromCurso(@PathVariable int idCurso) {
        return tareaRepository.queryAllByIdCurso(idCurso);
    }

    /**
     * Crear una tarea a un curso
     * @param map json con la siguiente estructura
     *            {
     *              "nombre": String,
     *              "descripcion": String,
     *              "curso": {"idCurso": int},
     *              "fechaLimite": Date,
     *              "rubrica": {},
     *              "roles": [string]
     *            }
     * @return tarea insertada
     */
    @PostMapping("/tareas")
    Tarea newTarea(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        var tarea = new Tarea(json.getString("descripcion"));
        LocalDateTime fechaLimite = LocalDateTime.parse(json.getString("fechaLimite"), formatter);
        tarea.setNombre(json.getString("nombre"));
        var cursoJson = json.getJSONObject("curso");
        var curso = cursoRepository.findById(cursoJson.getInt("idCurso")).orElseThrow();
        float peso = json.getFloat("peso");
        tarea.setPeso(peso);
        tarea.setCurso(curso);
        tarea.setFechaLimite(fechaLimite);
        tareaService.asignarRubrica(tarea, json.getJSONObject("rubrica"));
        tareaService.asignarRoles(tarea, json.getJSONArray("rolesEncargados"));
        tareaService.verificarVistoBueno(tarea);
        var esExp = json.getBoolean("esExposicion");
        tarea.setEsExposicion(esExp);
        log.info("Insertando tarea...");
        tareaRepository.save(tarea);
        var alumnos = alumnoRepository.queryAllByCursoAndPlanTesisIsNotNull(curso);
        for(Alumno al: alumnos){
            var tareaEntrega = tareaService.crearEntregaVacia(tarea,al);
            tareaEntregaRepository.save(tareaEntrega);
        }
        return tarea;
    }

    /**
     * Modificar una tarea
     * @param map json con la siguiente estructura
     *            {
     *              "nombre": String,
     *              "descripcion": String,
     *              "curso": {"idCurso": int},
     *              "fechaLimite": Date,
     *              "rubrica": {},
     *              "roles": [string]
     *            }
     * @return tarea modificada
     */
    @PostMapping("/tareas/modificar")
    Tarea modificarTarea(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var tarea = tareaRepository.queryByTareaIdWithRubrica(json.getLong("id")).orElseThrow();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime fechaLimite = LocalDateTime.parse(json.getString("fechaLimite"), formatter);
        tarea.setNombre(json.getString("nombre"));
        tarea.setDescripcion(json.getString("descripcion"));
        tarea.setFechaLimite(fechaLimite);
        if (!LocalDateTime.now().isEqual(tarea.getFechaLimite())) {
            var tareaEntregas = tareaEntregaRepository.queryTareaEntregasByIdTarea(tarea.getId());
            for(TareaEntrega te: tareaEntregas){
                if (tarea.getFechaLimite().isAfter(LocalDateTime.now())) {
                    if(te.getListaDocumentos().size() == 0) te.setEstadoEntrega(EstadoEntrega.PENDIENTE);
                    else te.setEstadoEntrega(EstadoEntrega.REALIZADA);
                } else {
                    te.setEstadoEntrega(EstadoEntrega.FINALIZADA);
                }
                tareaEntregaRepository.save(te);
            }
        }
        tarea.setPeso(json.getFloat("peso"));
        var rubricaJson = json.getJSONObject("rubrica");
        tareaService.asignarRubrica(tarea, rubricaJson);
        tareaService.asignarRoles(tarea, json.getJSONArray("rolesEncargados"));

        tareaService.verificarVistoBueno(tarea);

        var esExp = json.getBoolean("esExposicion");
        tarea.setEsExposicion(esExp);
        tareaRepository.save(tarea);
        return tarea;
    }

    @PostMapping("/tareas/asignarTareaPadre")
    Tarea asignarTareaPadre(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var tareaPadre = tareaRepository.queryByIdWithEncargados(json.getLong("idTareaPadre")).orElseThrow();
        var tarea = tareaRepository.queryByIdWithEncargados(json.getLong("idTarea")).orElseThrow();
        tarea.setTareaPadre(tareaPadre);
        return tareaRepository.save(tarea);
    }

    @DeleteMapping("/tareas/eliminar/rubricaCriterio/{idCriterio}")
    public RubricaCriterio eliminarRubricaCriterio(@PathVariable Long idCriterio) {
        var crit = rubricaCriterioRepository.findById(idCriterio).orElseThrow();
        var retroSet = retroalimentacionRepository.queryByRubrica(crit.getRubrica());
        for (var retro: retroSet) {
            if (retro.getNotasObtenidas().containsKey(idCriterio)) return null;
        }
        rubricaCriterioRepository.delete(crit);
        return crit;
    }

    /**
     * Eliminacion logica de una tarea
     * @param id id de una tarea
     * @return tarea eliminada
     */
    @DeleteMapping("/tareas/eliminar/{id}")
    Tarea eliminarTarea(@PathVariable Long id) {
        var tarea = tareaRepository.queryByIdAndActivoIsTrue(id).orElseThrow();
        tarea.setTareaEntregas(tareaEntregaRepository.queryTareaEntregasByIdTarea(tarea.getId()));
        if (!tarea.isVisible()){
            for (TareaEntrega tarEnt : tarea.getTareaEntregas()){
                tarEnt.setActivo(false);
                tareaEntregaRepository.save(tarEnt);
            }
        }
        tarea.setActivo(false);
        return tareaRepository.save(tarea);
    }

    @PostMapping("/tareas/agregarRetroalimentacion")
    Retroalimentacion agregarRetroalimentacion(@RequestBody Map<String,Object>map){
        var json = new JSONObject(map);
        var tareaEntrega = tareaEntregaRepository.findById(json.getLong("idTareaEntrega")).orElseThrow();
        var profesor = profesorRepository.findById(json.getLong("idProfesor")).orElseThrow();
        var retroalimentacion = new Retroalimentacion();
        retroalimentacion.setProfesor(profesor);
        retroalimentacion.setTareaEntrega(tareaEntrega);
        retroalimentacion.setRubrica(tareaEntrega.getTarea().getRubrica());
        return retroalimentacionRepository.save(retroalimentacion);
    }

    @PostMapping("/tareas/modificarRetroalimentacion")
    Retroalimentacion modificarRetroalimentacion(@RequestBody Map<String,Object> map) {
        var json = new JSONObject(map);
        var retroalimentacion =
                retroalimentacionRepository.queryByIdFetchNotasAndDocumentos(json.getLong("idRetroalimentacion")).orElseThrow();
        float tmp = 0;
        for (Object obj: json.getJSONArray("notas")) {
            var json2 = new JSONObject(obj.toString());
            long idCriterio = json2.getLong("idCriterio");
            var crit = rubricaCriterioRepository.findById(idCriterio).orElseThrow();
            float nota = json2.getFloat("nota");
            // se permiten notas negativas
            if (nota > crit.getNotaMaxima()) nota = crit.getNotaMaxima();
            retroalimentacion.getNotasObtenidas().put(idCriterio, nota);
            tmp += nota;
        }
        retroalimentacion.setNotaFinal(tmp);
        var set = new HashSet<Documento>();
        for (Object obj: json.getJSONArray("documentos")) {
            Long idDocumento = Long.valueOf(obj.toString());
            var documento = documentoRepository.queryByIdAndActivoIsTrue(idDocumento).orElseThrow();
            set.add(documento);
        }
        retroalimentacion.setListaDocumentos(set);
        retroalimentacionRepository.save(retroalimentacion);
        tareaService.actualizarNota(retroalimentacion.getTareaEntrega());
        return retroalimentacion;
    }

    @GetMapping("/tareas/entregas/{idTareaEntrega}/retroalimentaciones")
    Set<Retroalimentacion> getRetroalimentacionoesFromEntrega(@PathVariable Long idTareaEntrega) {
        var entrega = tareaEntregaRepository.findById(idTareaEntrega).orElseThrow();
        return retroalimentacionRepository.queryByTareaEntregaWithNotasAndDocumentos(entrega);
    }

    @GetMapping("/tareas/obtenerRetroalimentacion/{idTareaEntrega}")
    List<Retroalimentacion> getRetroalimentacionesfromTareaEntrega(@PathVariable Long idTareaEntrega) {
        return retroalimentacionRepository.queryAllWithDocumentsByTareaEntrega_Id(idTareaEntrega);
    }

    @PostMapping("/tareas/eliminarEntregaDocumento")
    TareaEntrega eliminarEntregaDocumento(@RequestBody Map<String,Object>map) {
        var json = new JSONObject(map);
        Long idDocumento = json.getLong("idDocumento");
        Long idTareaEntrega = json.getLong("idTareaEntrega");
        var documento = documentoRepository.queryByIdAndActivoIsTrue(idDocumento).orElseThrow();
        var tareaEntrega = tareaEntregaRepository.queryByIdWithDocumentos(idTareaEntrega).orElseThrow();
        if(LocalDateTime.now().isAfter(tareaEntrega.getTarea().getFechaLimite())) {
            tareaEntrega.setEstadoEntrega(EstadoEntrega.FINALIZADA);
            return tareaEntregaRepository.save(tareaEntrega);
        }
        tareaEntrega.getListaDocumentos().remove(documento);
        tareaEntrega.setUltimaModificacion(Instant.now());
        if(tareaEntrega.getListaDocumentos().size() == 0) tareaEntrega.setEstadoEntrega(EstadoEntrega.PENDIENTE);
        return tareaEntregaRepository.save(tareaEntrega);
    }

    @GetMapping("/tareas/obtenerRubrica/{idRubrica}")
    public Rubrica obtenerRubricaPorId(@PathVariable Long idRubrica) {
        return rubricaRepository.queryById(idRubrica);
    }

    @GetMapping("/tareas/obtenerDocumentoTareaEntrega/alumno/{idAlumno}/tarea/{idTarea}")
    Set<Documento> obtenerDocumentoPorTareaEntrega(@PathVariable long idAlumno,@PathVariable long idTarea){
        var tareaEntrega= tareaEntregaRepository.queryTareaEntregaByAlumno_IdAndTarea_Id
                (idAlumno,idTarea).orElseThrow();
        return tareaEntrega.getListaDocumentos();
    }

    @GetMapping("/tareas/obtenerTareaEntrega/{idAlumno}/{idTarea}")
    TareaEntrega obtenerTareaEntregaDeAlumno(@PathVariable long idAlumno,@PathVariable long idTarea) {
        return tareaEntregaRepository.queryTareaEntregaByAlumno_IdAndTarea_Id(idAlumno,idTarea).orElseThrow();
    }

    @PostMapping("/tareas/agregarDocumentoTareaEntrega")
    TareaEntrega agregarDocumentoTareaEntrega(@RequestBody Map<String,Object>map){
        var json = new JSONObject(map);
        Long idTareaEntrega = json.getLong("idTareaEntrega");
        var tareaEntrega = tareaEntregaRepository.queryByIdWithDocumentos(idTareaEntrega).orElseThrow();
        if(LocalDateTime.now().isAfter(tareaEntrega.getTarea().getFechaLimite())){
            tareaEntrega.setEstadoEntrega(EstadoEntrega.FINALIZADA);
            return tareaEntregaRepository.save(tareaEntrega);
        }
        long idDocumento = json.getLong("idDocumento");
        var documento = documentoRepository.queryByIdAndActivoIsTrue(idDocumento).orElseThrow();
        tareaEntrega.getListaDocumentos().add(documento);
        tareaEntrega.setUltimaModificacion(Instant.now());
        tareaEntrega.setEstadoEntrega(EstadoEntrega.REALIZADA);
        return tareaEntregaRepository.save(tareaEntrega);
    }

    @GetMapping("/tareas/encargadas/{idCurso}/{rolStr}")
    public List<Tarea> obtenerTareasEncargadas(@PathVariable int idCurso, @PathVariable String rolStr) {
        var rol = rolRepository.queryByNombre(rolStr.toUpperCase()).orElseThrow();
        return tareaRepository.queryAllTareasByRolEncargado(idCurso, rol);
    }

    @GetMapping("/tareas/entregasEncargadas/{idTarea}/{idProfesor}")
    public List<TareaEntrega> obtenerEntregasEncargadas(@PathVariable Long idTarea, @PathVariable Long idProfesor) {
        var prof = profesorRepository.findById(idProfesor).orElseThrow();
        var tesis = planTesisRepository.queryAllTesisFromProfesor(prof);
        return tareaEntregaRepository.queryEntregasByTareaIdAndPlanTesisId(idTarea, tesis);
    }

    @PostMapping("tareas/crearTareaAvances")
    public TareaAvances crearTareaAvances(@RequestBody Map<String,Object>map){
        var json = new JSONObject(map);
        long idTarea = json.getLong("idTarea");
        long idAlumno = json.getLong("idAlumno");
        var tareaEntrega = tareaEntregaRepository.queryTareaEntregaByAlumno_IdAndTarea_Id(
                idAlumno,idTarea).orElseThrow();
        var tareaAvances = new TareaAvances();
        tareaAvances.setListaAvances(new HashSet<>());
        tareaAvances.setEntrega(tareaEntrega);
        tareaEntrega.setAvancesOld(tareaAvances);
        tareaAvancesRepository.save(tareaAvances);
        tareaEntregaRepository.save(tareaEntrega);
        return tareaAvances;

    }

    @PostMapping("tareas/agregarAvance")
    public TareaAvance agregarTareaAvance(@RequestBody Map<String,Object>map){
        var json = new JSONObject(map);
        long idTarea = json.getLong("idTarea");
        long idAlumno = json.getLong("idAlumno");
        var tareaEntrega = tareaEntregaRepository.queryByTareaEntregaByAlumnoAndTareaAvances(
                idTarea,idAlumno).orElseThrow();
        var tareasAvances = tareaEntrega.getAvancesOld();
        var tareaAvance = new TareaAvance();
        long idDocumento = json.getLong("idDocumento");
        var documento = documentoRepository.queryByIdAndActivoIsTrue(idDocumento).orElseThrow();
        tareaAvance.setDocumentos(new HashSet<>());
        tareaAvance.getDocumentos().add(documento);
        tareaAvance.setAvance(tareasAvances);
        tareaAvanceRepository.save(tareaAvance);
        tareasAvances.getListaAvances().add(tareaAvance);
        tareaAvancesRepository.save(tareasAvances);
        return tareaAvance;
    }

    @GetMapping("/tareas/entregas/{idAlumno}")
    public Set<TareaEntrega> getEntregasFromAlumno(@PathVariable Long idAlumno) {
        var alumn = alumnoRepository.findById(idAlumno).orElseThrow();
        return tareaEntregaRepository.queryTareaEntregasByAlumno(alumn);
    }

    @GetMapping("/tareas/entregasActivas/{idAlumno}")
    public List<Tarea> getEntregasActivasFromAlumno(@PathVariable Long idAlumno) {
        var alumn = alumnoRepository.findById(idAlumno).orElseThrow();
        var tareas = new ArrayList<Tarea>();
        var entregas = tareaEntregaRepository.queryTareaEntregasByAlumno(alumn);
        for (var entrega: entregas) {
            var tarea = entrega.getTarea();
            if (tarea.isVisible()) tareas.add(tarea);
        }
        return tareas;
    }

    @PostMapping("/tareas/entrega/modificarVistoBueno")
    public TareaEntrega setVistoBuenoTareaEntrega(@RequestBody Map<String,Object>map){
        var json = new JSONObject(map);
        long idTareaEntrega = json.getLong("idTareaEntrega");
        boolean vistoBueno = json.getBoolean("vistoBueno");
        var tareaEntrega = tareaEntregaRepository.findById(idTareaEntrega).orElseThrow();
        tareaEntrega.setVistoBueno(vistoBueno);
        return tareaEntregaRepository.save(tareaEntrega);
    }

    @PostMapping("/tareas/entrega/{idTareaEntrega}/actualizarAvance")
    public void actualizarAvance(@PathVariable Long idTareaEntrega) {
        var entrega = tareaEntregaRepository.findById(idTareaEntrega).orElseThrow();
        entrega.setUltimaModificacion(Instant.now());
        tareaEntregaRepository.save(entrega);
    }
}
