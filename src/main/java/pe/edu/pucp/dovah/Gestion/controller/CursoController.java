/*
 * Nombre del archivo: CursoController.java
 * Fecha de creacion: 21-09-2022
 * Autor: Victor Avalos
 * Descripción: Definición de los metodos usados para la clase Curso
 */
package pe.edu.pucp.dovah.Gestion.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.exception.CursoNotFoundException;
import pe.edu.pucp.dovah.Gestion.exception.EspecialidadNotFoundException;
import pe.edu.pucp.dovah.Gestion.exception.FacultadNotFoundException;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.Gestion.repository.EspecialidadRepository;
import pe.edu.pucp.dovah.Gestion.repository.SemestreRepository;
import pe.edu.pucp.dovah.Gestion.service.CursoService;
import pe.edu.pucp.dovah.RRHH.repository.AlumnoRepository;
import pe.edu.pucp.dovah.RRHH.repository.ProfesorRepository;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;
import pe.edu.pucp.dovah.asignaciones.model.Documento;

import java.util.*;

@RequestMapping("/api/v1/")
@RestController
public class CursoController {

    private final CursoRepository cursoRepository;
    private final SemestreRepository semestreRepository;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;
    private final CursoService cursoService;
    private final RolRepository rolRepository;

    private final static Logger log = LoggerFactory.getLogger(CursoController.class);

    public CursoController(CursoRepository repositoryCurso,
                           UsuarioRepository usuarioRepository, SemestreRepository semestreRepository,
                           EspecialidadRepository especialidadRepository,
                           AlumnoRepository alumnoRepository,
                           ProfesorRepository profesorRepository,
                           CursoService cursoService,
                           RolRepository rolRepository) {
        this.cursoRepository = repositoryCurso;
        this.usuarioRepository = usuarioRepository;
        this.semestreRepository = semestreRepository;
        this.especialidadRepository = especialidadRepository;
        this.alumnoRepository = alumnoRepository;
        this.profesorRepository = profesorRepository;
        this.cursoService = cursoService;
        this.rolRepository = rolRepository;
    }

    /*Listar todos los cursos*/
    @GetMapping("/curso")
    List<Curso> all(){

        return cursoRepository.queryAllByActivoIsTrue();

    }

    /*Buscar un curso*/
    @GetMapping("/curso/{id}")
    Curso obtenerCursoPorId(@PathVariable int id){

        return cursoRepository.findByIdCursoAndActivoIsTrue(id).orElseThrow(() -> new CursoNotFoundException(id));
    }

    /*Insertar un curso*/
    @PostMapping("/curso")
    Curso nuevoCurso(@RequestBody Map<String,Object> nuevoCurso){
        log.info("Agregando curso");
        var json = new JSONObject(nuevoCurso);
        int idEspecialidad = json.getInt("idEspecialidad");
        int idSemestre = json.getInt("idSemestre");
        var semestre = semestreRepository.findByIdSemestreAndActivoIsTrue(idSemestre).orElseThrow(()
                ->new FacultadNotFoundException(idSemestre));
        var especialidad = especialidadRepository.findByIdEspecialidadAndActivoIsTrue(idEspecialidad).
                orElseThrow(()->new EspecialidadNotFoundException(idEspecialidad));
        var curso = new Curso(json.getString("clave"),json.getString("nombre"), especialidad, semestre);
        var requisitoId = json.getInt("idCursoRequisito");
        if (requisitoId != 0) {
            var req = cursoRepository.findById(requisitoId).orElseThrow();
            curso.setCursoRequisito(req);
            cursoRepository.save(curso);
            cursoService.obtenerDatosDeCursoRequisito(curso);
        }
        return cursoRepository.save(curso);
    }

    /*Actualizar un curso*/
    @PostMapping("/curso/actualizar")
    Curso actualizarCurso(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        int id = json.getInt("idCurso");
        String clave = json.getString("clave");
        String nombre = json.getString("nombre");
        var curso = cursoRepository.findByIdCursoAndActivoIsTrue(id).orElseThrow(()
                ->new CursoNotFoundException(id));
        log.info(String.format("Actualizando atributos de curso con id '%d'",
                curso.getIdCurso()));
        curso.setNombre(nombre);
        curso.setClave(clave);
        return cursoRepository.save(curso);
    }

    /*Listar cursos por idEspecialidad y idSemestre*/
    @GetMapping("/curso/especialidad/{idEspecialidad}/semestre/{idSemestre}")
    List<Curso> listarCursosPorEspecialidadPorSemestre(@PathVariable int idEspecialidad, @PathVariable int idSemestre) {
        return cursoRepository.findByEspecialidad_IdEspecialidadAndSemestre_IdSemestreAndActivoIsTrue(idEspecialidad,idSemestre);
    }


    /*Listar cursos por especialidad*/
    @GetMapping("/curso/ListarPorEspecialidad/{id}")
    List<Curso> listarCursosPorEspecialidad(@PathVariable int id) {
        return cursoRepository.findByEspecialidad_IdEspecialidadAndActivoIsTrue(id);
    }

    /*Listar cursos por semestre*/
    @GetMapping("/curso/ListarPorSemestre/{id}")
    List<Curso> listarCursosPorSemestre(@PathVariable int id) {
        return cursoRepository.findBySemestre_IdSemestreAndActivoIsTrue(id);
    }

    /*Eliminar un curso*/
    @PostMapping("/curso/eliminar")
    Curso eliminarCurso(@RequestBody Map<String, Object> map){
        var json = new JSONObject(map);
        int idCurso = json.getInt("idCurso");
        var curso = cursoRepository.findByIdCursoAndActivoIsTrue(idCurso)
                .orElseThrow(()-> new CursoNotFoundException(idCurso));
        log.info(String.format("Eliminando curso con id '%d'", curso.getIdCurso()));
        curso.setActivo(false);
        return cursoRepository.save(curso);
    }

    @GetMapping("/curso/listarCursosPorUsuario/{id}/{rol}")
    List<Curso> listarUsuarioPorCurso(@PathVariable int id, @PathVariable String rol){
        var usr = usuarioRepository.findById(id).orElseThrow();
        var role = rolRepository.queryByNombre(rol.toUpperCase()).orElseThrow();
        List<Curso> cursos = new ArrayList<>();
        if (rol.equals("alumno")) {
            var alumnos = alumnoRepository.queryByUsuarioAndActivoIsTrue(usr);
            for(var alumn: alumnos) cursos.add(alumn.getCurso());
        } else {
            var profs = profesorRepository.queryAllByUsuarioWithRol(usr, role);
            for(var prof: profs) cursos.add(prof.getCurso());
        }
        return cursos;
    }

    @PostMapping("/curso/agregarDocumentoGeneral")
    Curso agregarDocumentoGeneral(@RequestBody Map<String, Object> map){
        var json = new JSONObject(map);
        return cursoService.agregarEliminarDocumento(json, "agregar");
    }

    @GetMapping("/curso/listarDocumentos/{id}")
    List<Documento>listarDocumentosGeneralesPorCurso(@PathVariable int id){
        var curso = cursoRepository.queryCursoWithDocumentos(id).orElseThrow();
        return curso.getDocumentosGenerales();
    }

    @PostMapping("/curso/eliminarDocumentoGeneral")
    Curso eliminarDocumentoGeneral(@RequestBody Map<String, Object> map){
        var json = new JSONObject(map);
        return cursoService.agregarEliminarDocumento(json, "eliminar");
    }

    @GetMapping("/curso/getPasados/{idEspecialidad}")
    public List<String> getCursosPasados(@PathVariable Integer idEspecialidad) {
        return cursoRepository.getCursosPasados(idEspecialidad);
    }
}
