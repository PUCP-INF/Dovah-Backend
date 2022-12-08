/*
 * Nombre del archivo: PlanTesisController
 * Fecha de creación: 18/10/2022 , 11:46
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.PlanTesis.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.Gestion.repository.EspecialidadRepository;
import pe.edu.pucp.dovah.PlanTesis.model.*;
import pe.edu.pucp.dovah.PlanTesis.repository.*;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.RRHH.repository.AlumnoRepository;
import pe.edu.pucp.dovah.RRHH.repository.ProfesorRepository;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;
import pe.edu.pucp.dovah.mensajes.model.Hilo;
import pe.edu.pucp.dovah.mensajes.repository.HiloRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequestMapping("/api/v1")
@RestController
public class PlanTesisController {
    private final PlanTesisRepository planTesisRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final AreaPlanTesisRepository areaPlanTesisRepository;
    private final PeriodoPlanTesisRepository periodoPlanTesisRepository;
    private final HiloRepository hiloRepository;
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;
    private final CursoRepository cursoRepository;
    private final ProfesorInscritoPlanTesisRepository profesorInscritoPlanTesisRepository;
    private final EspecialidadRepository especialidadRepository;
    private final static Logger log = LoggerFactory.getLogger(PlanTesisController.class);
    
    public PlanTesisController(PlanTesisRepository planTesisRepository,
                               UsuarioRepository usuarioRepository,
                               RolRepository rolRepository,
                               AreaPlanTesisRepository areaPlanTesisRepository,
                               HiloRepository hiloRepository,
                               AlumnoRepository alumnoRepository,
                               CursoRepository cursoRepository,
                               ProfesorInscritoPlanTesisRepository profesorInscritoPlanTesisRepository,
                               ProfesorRepository profesorRepository,
                               PeriodoPlanTesisRepository periodoPlanTesisRepository,
                               EspecialidadRepository especialidadRepository) {
        this.areaPlanTesisRepository = areaPlanTesisRepository;
        this.planTesisRepository = planTesisRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.periodoPlanTesisRepository = periodoPlanTesisRepository;
        this.alumnoRepository = alumnoRepository;
        this.hiloRepository = hiloRepository;
        this.cursoRepository = cursoRepository;
        this.profesorInscritoPlanTesisRepository = profesorInscritoPlanTesisRepository;
        this.profesorRepository = profesorRepository;
        this.especialidadRepository = especialidadRepository;
    }

    @GetMapping("/planTesis")
    List<PlanTesis> listarTodos(){
        return planTesisRepository.findAll();
    }

    @GetMapping("/planTesis/porPeriodo/{id}")
    List<PlanTesis> listarTodosPorPeriodo(@PathVariable Long id) {
        return planTesisRepository.queryByPeriodoPlanTesis_Id(id);
    }

    @GetMapping("/planTesis/areas/{idEspecialidad}")
    Set<AreaPlanTesis> listarAreasTesis(@PathVariable int idEspecialidad){
        return areaPlanTesisRepository.queryAllByEstadoIsTrueAndEspecialidad_IdEspecialidad(idEspecialidad);
    }

    @PostMapping("/planTesis/areas/nuevo")
    AreaPlanTesis insertarAreaTesis(@RequestBody Map<String,Object> map) {
        var json = new JSONObject(map);
        var especialidad = especialidadRepository.findByIdEspecialidadAndActivoIsTrue(
                json.getInt("idEspecialidad")).orElseThrow();
        var area = new AreaPlanTesis(json.getString("nombre"),especialidad);
        return areaPlanTesisRepository.save(area);
    }

    @PostMapping("/planTesis/areas/modificar")
    AreaPlanTesis modificarAreaTesis(@RequestBody Map<String,Object> map) {
        var json = new JSONObject(map);
        int idAreaPlanTesis = json.getInt("idAreaPlanTesis");
        var nombre = json.getString("nombre");
        var area = areaPlanTesisRepository.queryByIdAndEstadoIsTrue(idAreaPlanTesis).orElseThrow();
        area.setNombre(nombre);
        return areaPlanTesisRepository.save(area);
    }

    @PostMapping("/planTesis/areas/eliminar")
    AreaPlanTesis eliminarAreaTesis(@RequestBody Map<String,Object> map) {
        var json = new JSONObject(map);
        int idAreaPlanTesis = json.getInt("idAreaPlanTesis");
        var area = areaPlanTesisRepository.queryByIdAndEstadoIsTrue(idAreaPlanTesis).orElseThrow();
        area.setEstado(false);
        return areaPlanTesisRepository.save(area);
    }



    @GetMapping("/planTesis/{id}")
    PlanTesis getOne(@PathVariable int id) {
        return planTesisRepository.findById(id).orElseThrow();
    }

    @GetMapping("/planTesis/listar/alumnos/{idCurso}/{idPlanTesis}")
    Set<Alumno> getAlumnosFromTesis(@PathVariable int idCurso, @PathVariable int idPlanTesis) {
        var curso = cursoRepository.findById(idCurso).orElseThrow();
        var pt   = planTesisRepository.findById(idPlanTesis).orElseThrow();
        return alumnoRepository.queryAllByCursoAndPlanTesis(curso, pt);
    }

    @GetMapping("/planTesis/listar/alumnosDisponibles/{idCurso}")
    Set<Alumno> getAlumnosDisponiblesFromCurso(@PathVariable int idCurso) {
        var curso = cursoRepository.findById(idCurso).orElseThrow();
        return alumnoRepository.queryAllByCursoAndPlanTesisIsNull(curso);
    }

    @GetMapping("/planTesis/listar/profesores/{idCurso}/{idPlanTesis}")
    Set<ProfesorInscritoPlanTesis> getProfesoresFromTesis(@PathVariable int idCurso, @PathVariable int idPlanTesis) {
        var curso = cursoRepository.findById(idCurso).orElseThrow();
        var pt = planTesisRepository.findById(idPlanTesis).orElseThrow();
        return profesorInscritoPlanTesisRepository.queryAllByPlanTesisAndCurso(pt, curso);
    }

    @GetMapping("/planTesis/listar/profesoresDisponibles/{idCurso}/{idPlanTesis}")
    Set<Profesor> getProfesoresDisponibles(@PathVariable int idCurso, @PathVariable int idPlanTesis) {
        var curso = cursoRepository.findById(idCurso).orElseThrow();
        var pt = planTesisRepository.findById(idPlanTesis).orElseThrow();
        return profesorRepository.queryAllInCursoNotInPlanTesis(curso, pt);
    }

    @PostMapping("/planTesis")
    PlanTesis nuevoTema(@RequestBody Map<String,Object> nuevoTema){
        log.info("Agregando nuevo tema");
        var json = new JSONObject(nuevoTema);
        var periodo = periodoPlanTesisRepository.queryPeriodoActivo();
        if (periodo == null) {
            return null;
        } else {
            if (LocalDate.now().isBefore(periodo.getFechaInicio())) {
                return null;
            } else if (LocalDate.now().isAfter(periodo.getFechaFin())) {
                return null;
            }
        }
        var area = areaPlanTesisRepository.findById(json.getInt("idAreaEsp")).orElseThrow();
        var tema = new PlanTesis(json.getString("titulo"), area, json.getString("descripcion"));
        var detallesAdicionales = json.getString("detallesAdicionales");
        tema.setDetallesAdicionales(detallesAdicionales);
        var codigo = json.getString("codigoPUCP");
        var usuario = usuarioRepository.findByCodigoPUCPContainingAndActivoIsTrue(codigo).orElseThrow();
        var rol = rolRepository.queryByNombre("ALUMNO").orElseThrow();
        if(usuario.getListaRoles().contains(rol) && !usuario.getEspecialidad().isAlumnosProponenTesis()) return null;
        var hilo = new Hilo();
        hiloRepository.save(hilo);
        tema.setProponiente(usuario);
        tema.setPeriodoPlanTesis(periodo);
        tema.setFacultad(usuario.getFacultad());
        tema.setHilo(hilo);
        return planTesisRepository.save(tema);
    }

    /**
     *
     * @param map json con la siguiente estructura
     *            {
     *              "id": int,
     *              "idAreaEsp": int,
     *              "descripcion": String,
     *              "detallesAdicionales": String,
     *              "titulo": String
     *            }
     * @return plan tesis modificado
     */
    @PostMapping("/planTesis/modificar")
    PlanTesis modificar(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var tesis = planTesisRepository.findById(json.getInt("id")).orElseThrow();
        var area = areaPlanTesisRepository.findById(json.getInt("idAreaEsp")).orElseThrow();
        tesis.setAreaEspecialidad(area);
        tesis.setDescripcion(json.getString("descripcion"));
        tesis.setDetallesAdicionales(json.getString("detallesAdicionales"));
        tesis.setTitulo(json.getString("titulo"));
        return planTesisRepository.save(tesis);
    }

    @PostMapping("/planTesis/modificarEstado")
    PlanTesis modificarEstado(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var tesis = planTesisRepository.findById(json.getInt("id")).orElseThrow();
        tesis.setEstado(EstadoPlanTesis.valueOf(json.getString("estado")));
        return planTesisRepository.save(tesis);
    }

    @GetMapping("/planTesis/listarPlanTesisPorUsuario/{id}")
    List<PlanTesis> listarPlanTesisPorUsuario(@PathVariable int id){
        return planTesisRepository.findByProponiente_IdUsuario(id);
    }

    @PostMapping("/planTesis/iniciarPeriodoPropuestas")
    PeriodoPlanTesis iniciarPeriodoPropuestas(@RequestBody Map<String,Object> map) {
        var json = new JSONObject(map);
        var periodo = new PeriodoPlanTesis();
        if (periodoPlanTesisRepository.queryPeriodoActivo() != null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDate fechaInicio = LocalDate.parse(json.getString("fechaInicio"), formatter);
        LocalDate fechaFin = LocalDate.parse(json.getString("fechaFin"), formatter);
        periodo.setFechaInicio(fechaInicio);
        periodo.setFechaFin(fechaFin);
        return periodoPlanTesisRepository.save(periodo);
    }

    @GetMapping("/planTesis/getAllPeriodos")
    List<PeriodoPlanTesis> getAllPeriodos() {
        return periodoPlanTesisRepository.findAll(Sort.by(Sort.Direction.DESC, "fechaCreacion"));
    }

    @PostMapping("/planTesis/finalizarPeriodo/{id}")
    PeriodoPlanTesis finalizarPeriodo(@PathVariable long id) {
        var periodo = periodoPlanTesisRepository.findById(id).orElseThrow();
        periodo.setActivo(false);
        return periodoPlanTesisRepository.save(periodo);
    }

    @GetMapping("/planTesis/getPeriodoActivo")
    PeriodoPlanTesis getPeriodoActivo() {
        return periodoPlanTesisRepository.queryPeriodoActivo();
    }

    @GetMapping("/planTesis/listarPlanesTesisPorPeriodo/{idPeriodo}")
    List<PlanTesis> getPlanesTesisPorPeriodoActivo(@PathVariable long idPeriodo){
        return planTesisRepository.queryByPeriodoAndEstadoAprobado(idPeriodo);
    }

    @GetMapping("/planTesis/listarInscritosPorPlanTesis/{idPlanTesis}")
    List<Usuario> obtenerListarInscritosPorPlanTesis(@PathVariable int idPlanTesis){
        var usuarios = new ArrayList<Usuario>();
        var planTesis = planTesisRepository.queryByIdPlanTesisAndAlumnos(idPlanTesis).orElseThrow();
        for(Alumno al : planTesis.getAlumnos()){
            usuarios.add(al.getUsuario());
        }
        planTesis = planTesisRepository.queryByIdPlanTesisAndProfesores(idPlanTesis).orElseThrow();
        for(ProfesorInscritoPlanTesis pipt : planTesis.getProfesores()){
            var usuario = pipt.getProfesor().getUsuario();
            usuario.setListaRoles(pipt.getRoles());
            usuarios.add(usuario);
        }
        return usuarios;

    }

    @GetMapping("/planTesis/obtenerTesisPorInscrito/{idUsuario}")
    Set<PlanTesis> obtenerTesisConUsuarioInscrito(@PathVariable int idUsuario) {
        var usr = usuarioRepository.findById(idUsuario).orElseThrow();
        return planTesisRepository.queryAllTesisWithUsuarioInscrito(usr);
    }

    @GetMapping("/planTesis/obtenerProfesoresAsociadosAlumnoPlanTesis/{idPlanTesis}")
    Set<ProfesorInscritoPlanTesis> obtenerProfesoresAsociadosAlumnoPlanTesis(@PathVariable int idPlanTesis){
        var planTesis = planTesisRepository.queryByIdPlanTesisAndProfesores(idPlanTesis).orElseThrow();
        return planTesis.getProfesores();
    }

    @GetMapping("/planTesis/listarAlumnosPorPlanTesis/{idPlanTesis}")
    Set<Alumno> obtenerAlumnosInscritosPlanTesis(@PathVariable int idPlanTesis){
        var planTesis = planTesisRepository.queryByIdPlanTesisAndAlumnos(idPlanTesis).orElseThrow();
        Set<Alumno> listaAlumnos = new HashSet<>();
        for (Alumno al: planTesis.getAlumnos()){
            var idCurso = al.getCurso().getIdCurso();
            var curso = cursoRepository.findById(idCurso).orElseThrow();
            al.setCurso(curso);
            listaAlumnos.add(al);
        }
        return listaAlumnos;
    }

}
