/*
 * Nombre del archivo: AlumnoController
 * Fecha de creación: 15/10/2022 , 09:21
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.RRHH.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.repository.AlumnoRepository;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class AlumnoController {
    private final AlumnoRepository alumnoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final static Logger log = LoggerFactory.getLogger(AlumnoController.class);
    
    public AlumnoController(AlumnoRepository alumnoRepository,
                            UsuarioRepository usuarioRepository,
                            CursoRepository cursoRepository){
        this.cursoRepository = cursoRepository;
        this.alumnoRepository = alumnoRepository;
        this.usuarioRepository = usuarioRepository;
    }
    @GetMapping("/alumno")
    public List<Alumno> listarTodos(){

        return alumnoRepository.queryAllByActivoIsTrue();

    }
    @GetMapping("/alumno/especialidad/{idEspecialidad}")
    public List<Alumno> listarAlumnosPorEspecialidad(@PathVariable int idEspecialidad){
        return alumnoRepository.findByUsuario_Especialidad_IdEspecialidad(idEspecialidad);
    }

    @GetMapping("/alumno/{idAlumno}")
    public Alumno obtenerAlumnoPorId(@PathVariable Long idAlumno){
        return alumnoRepository.findById(idAlumno).orElseThrow();
    }

    @GetMapping("/alumno/{idUsuario}/{idCurso}")
    public Alumno getAlumnoFromUsuarioAndCurso(@PathVariable int idUsuario, @PathVariable int idCurso) {
        log.info("OBTENER ALUMNO");
        var usr = usuarioRepository.findById(idUsuario).orElseThrow();
        var curso = cursoRepository.findByIdCursoAndActivoIsTrue(idCurso).orElseThrow();
        return alumnoRepository.queryByUsuarioAndCurso(usr, curso).orElseThrow();
    }
}
