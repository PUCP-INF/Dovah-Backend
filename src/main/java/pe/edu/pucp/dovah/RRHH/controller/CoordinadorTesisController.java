/*
 * Nombre del archivo: CoordinadorTesisController
 * Fecha de creación: 2/10/2022 , 08:04
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.RRHH.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.Gestion.service.CursoService;
import pe.edu.pucp.dovah.PlanTesis.model.ProfesorInscritoPlanTesis;
import pe.edu.pucp.dovah.PlanTesis.repository.PlanTesisRepository;
import pe.edu.pucp.dovah.PlanTesis.repository.ProfesorInscritoPlanTesisRepository;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.RRHH.repository.AlumnoRepository;
import pe.edu.pucp.dovah.RRHH.repository.ProfesorRepository;
import pe.edu.pucp.dovah.RRHH.service.UsuarioService;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;
import pe.edu.pucp.dovah.asignaciones.model.Tarea;
import pe.edu.pucp.dovah.asignaciones.repository.TareaEntregaRepository;
import pe.edu.pucp.dovah.asignaciones.repository.TareaRepository;
import pe.edu.pucp.dovah.asignaciones.service.TareaService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@PreAuthorize("hasRole('ROLE_COORDINADOR')")
@RequestMapping("/api/v1/coordinador")
@RestController
public class CoordinadorTesisController {
    private final CursoRepository cursoRepository;
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;
    private final UsuarioService usuarioService;
    private final CursoService cursoService;
    private final PlanTesisRepository planTesisRepository;
    private final RolRepository rolRepository;
    private final TareaRepository tareaRepository;
    private final TareaService tareaService;
    private final ProfesorInscritoPlanTesisRepository profesorInscritoPlanTesisRepository;
    private final TareaEntregaRepository tareaEntregaRepository;
    private final static Logger log = LoggerFactory.getLogger(CoordinadorTesisController.class);

    public CoordinadorTesisController(CursoRepository cursoRepository,
                                      AlumnoRepository alumnoRepository,
                                      ProfesorRepository profesorRepository,
                                      PlanTesisRepository planTesisRepository,
                                      RolRepository rolRepository,
                                      ProfesorInscritoPlanTesisRepository profesorInscritoPlanTesisRepository,
                                      CursoService cursoService,
                                      UsuarioService usuarioService,TareaRepository tareaRepository,
                                      TareaEntregaRepository tareaEntregaRepository,
                                      TareaService tareaService) {
        this.alumnoRepository = alumnoRepository;
        this.profesorRepository = profesorRepository;
        this.cursoRepository = cursoRepository;
        this.usuarioService = usuarioService;
        this.planTesisRepository = planTesisRepository;
        this.rolRepository = rolRepository;
        this.cursoService = cursoService;
        this.profesorInscritoPlanTesisRepository = profesorInscritoPlanTesisRepository;
        this.tareaRepository =  tareaRepository;
        this.tareaService = tareaService;
        this.tareaEntregaRepository = tareaEntregaRepository;
    }

    @PostMapping("/curso/agregar")
    public void agregarAlumnoProfesorACurso(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var usr = new Usuario(json.getString("nombre"),json.getString("apellido"),
                json.getString("codigoPUCP"), json.getString("correo"),
                json.getString("password"));
        var roles = new HashSet<Rol>();
        if (json.has("roles")) {
            for (Object obj: json.getJSONArray("roles")) {
                var rol = rolRepository.queryByNombre(obj.toString()).orElseThrow();
                roles.add(rol);
            }
        }
        usr = usuarioService.newUser(usr);
        var curso = cursoRepository.findById(json.getInt("idCurso")).orElseThrow();
        var tipo = json.getString("tipo");
        cursoService.addAlumnoProfesorToCurso(usr, curso, tipo, roles);
        usuarioService.updateUserRoles(usr);
    }

    @PostMapping("/curso/modificar")
    public void modificarAlumnoProfesor(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var tipo = json.getString("tipo");
        var usr = usuarioService.modifyUser(json);
        var curso = cursoRepository.findById(json.getInt("idCurso")).orElseThrow();
        if (Objects.equals(tipo, "profesor")) {
            var prof = profesorRepository.queryByIdAndCurso(json.getLong("idProfesor"), curso).orElseThrow();
            var roles = new HashSet<Rol>();
            for (Object obj: json.getJSONArray("roles")) {
                var rol = rolRepository.queryByNombre(obj.toString()).orElseThrow();
                roles.add(rol);
            }
            prof.setRoles(roles);
            profesorRepository.save(prof);
        }
        usuarioService.updateUserRoles(usr);
    }

    @PostMapping("/curso/agregar/{tipoUsuario}/bulk")
    public void agregarAlumnosPorLotes(@RequestParam("file") MultipartFile file,
                                       @RequestParam("idCurso") Integer idCurso,
                                       @PathVariable String tipoUsuario) throws IOException {
        log.info("AGREGANDO " + tipoUsuario.toUpperCase() + "S");
        var curso = cursoRepository.findById(idCurso).orElseThrow();
        // Campus Virtual usa ISO_8859_1 por alguna razon >:v
        InputStreamReader isr = new InputStreamReader(file.getInputStream(), StandardCharsets.ISO_8859_1);
        BufferedReader br = new BufferedReader(isr);
        for (int i = 0; i < 7; i++) br.readLine();
        String line;
        while ((line = br.readLine()) != null) {
            var usr = new Usuario();
            var tokenizer = new StringTokenizer(line, "\t");
            while (tokenizer.hasMoreTokens()) {
                usr.setCodigoPUCP(tokenizer.nextToken());
                var nombre = tokenizer.nextToken().split(", ");
                usr.setApellido(nombre[0]);
                usr.setNombre(nombre[1]);
                tokenizer.nextToken(); // ignorar horario
                tokenizer.nextToken(); // ignorar especialidad
                usr.setCorreo(tokenizer.nextToken().split(",")[0]);
            }
            usr.setPassword("123456");
            usr = usuarioService.newUser(usr);
            cursoService.addAlumnoProfesorToCurso(usr, curso, tipoUsuario, null);
            usuarioService.updateUserRoles(usr);
        }
    }

    @DeleteMapping("/curso/eliminar")
    public void eliminarAlumnoCursoDeCurso(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var curso = cursoRepository.findById(json.getInt("idCurso")).orElseThrow();
        var tipo = json.getString("tipo");
        Usuario usr = null;
        if (Objects.equals(tipo, "profesor")) {
            var prof = profesorRepository.queryByIdAndCurso(json.getLong("idProfesor"), curso).orElseThrow();
            prof.setActivo(false);
            profesorRepository.save(prof);
            usr = prof.getUsuario();
        } else if (Objects.equals(tipo, "alumno")) {
            var alumn = alumnoRepository.queryByIdAndCurso(json.getLong("idAlumno"), curso).orElseThrow();
            alumn.setActivo(false);
            alumnoRepository.save(alumn);
            usr = alumn.getUsuario();
        }
        usuarioService.updateUserRoles(usr);
    }

    @GetMapping("/curso/{idCurso}/listar/alumno")
    public Set<Alumno> obtenerAlumnosDeCurso(@PathVariable int idCurso) {
        Set<Alumno> res = new HashSet<>();
        try {
            res = cursoRepository.queryByIdCursoWithAlumnos(idCurso).orElseThrow().getAlumnos();
        } catch (NoSuchElementException ignore) {}
        return res;
    }

    @GetMapping("/curso/{idCurso}/listarAlumnoConAsesor")
    public Set<Alumno> obtenerAlumnosDeCursoConAsesor(@PathVariable int idCurso) {
        Set<Alumno> res = new HashSet<>();
        try {
            res = cursoRepository.queryByIdCursoWithAlumnos(idCurso).orElseThrow().getAlumnos();
            for(Alumno al: res){
                if(al.getPlanTesis()  != null)
                    al.getPlanTesis().setProfesores(
                            profesorInscritoPlanTesisRepository.queryAllByPlanTesisAndCursoWhitRol(
                                    al.getPlanTesis(),al.getCurso()));


            }
        } catch (NoSuchElementException ignore) {}
        return res;
    }
    @GetMapping("/curso/{idCurso}/listar/profesor")
    public Set<Profesor> obtenerProfesoresDeCurso(@PathVariable int idCurso) {
        Set<Profesor> res = new HashSet<>();
        try {
            res = cursoRepository.queryByIdCursoWithProfesores(idCurso).orElseThrow().getProfesores();
        } catch (NoSuchElementException ignore) {}
        return res;
    }

    @PostMapping("/tesis/asignar/alumno")
    public Alumno asignarAlumnoTesis(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var alumn = alumnoRepository.findById(json.getLong("idAlumno")).orElseThrow();
        var tesis = planTesisRepository.findById(json.getInt("idPlanTesis")).orElseThrow();
        alumn.setPlanTesis(tesis);
        var tareas = tareaRepository.queryAllByIdCurso(alumn.getCurso().getIdCurso());
        for(Tarea tr: tareas){
            var entrega = tareaEntregaRepository
                    .queryTareaEntregaByAlumno_IdAndTarea_Id(alumn.getId(), tr.getId());
            if (entrega.isPresent()) {
                var tmp = entrega.get();
                tmp.setActivo(true);
                tareaEntregaRepository.save(tmp);
            } else {
                tareaService.crearEntregaVacia(tr,alumn);
            }
        }
        return alumnoRepository.save(alumn);
    }

    @PostMapping("/tesis/asignar/profesor")
    public Profesor asignarProfesorTesis(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var prof = profesorRepository.findById(json.getLong("idProfesor")).orElseThrow();
        var tesis = planTesisRepository.findById(json.getInt("idPlanTesis")).orElseThrow();
        var roles = new HashSet<Rol>();
        for (Object obj: json.getJSONArray("roles")) {
            roles.add(rolRepository.queryByNombre(obj.toString()).orElseThrow());
        }
        var tmp = profesorInscritoPlanTesisRepository.queryByProfesorAndPlanTesis(prof, tesis);
        ProfesorInscritoPlanTesis insc;
        if (tmp.isPresent()) {
            insc = tmp.get();
            insc.setRoles(roles);
        } else {
            insc = new ProfesorInscritoPlanTesis(prof, roles, tesis);
        }
        profesorInscritoPlanTesisRepository.save(insc);
        return prof;
    }

    @DeleteMapping("/tesis/eliminar/alumno/{idAlumno}")
    public void eliminarAlumnoTesis(@PathVariable Long idAlumno) {
        var alumn = alumnoRepository.findById(idAlumno).orElseThrow();
        var tesis = planTesisRepository
                .queryByIdPlanTesisAndAlumnos(alumn.getPlanTesis().getId()).orElseThrow();
        alumn.setPlanTesis(null);
        tesis.getAlumnos().remove(alumn);
        // eliminar entregas
        var entregas = tareaEntregaRepository.queryTareaEntregasByAlumno(alumn);
        for (var entrega: entregas) {
            entrega.setActivo(false);
            tareaEntregaRepository.save(entrega);
        }
        planTesisRepository.save(tesis);
        alumnoRepository.save(alumn);
    }

    @DeleteMapping("/tesis/eliminar/profesor/{idProfesor}/{idPlanTesis}")
    public void eliminarProfesorTesis(@PathVariable Long idProfesor, @PathVariable Integer idPlanTesis) {
        var prof = profesorRepository.findById(idProfesor).orElseThrow();
        var tesis = planTesisRepository.queryByIdPlanTesisAndAlumnos(idPlanTesis).orElseThrow();
        var inscrito = profesorInscritoPlanTesisRepository
                .queryByProfesorAndPlanTesis(prof, tesis).orElseThrow();
        profesorInscritoPlanTesisRepository.delete(inscrito);
    }
}
