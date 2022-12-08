package pe.edu.pucp.dovah.RRHH.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.RRHH.repository.ProfesorRepository;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;

import java.util.HashSet;
import java.util.Set;

/*
 * Nombre del archivo: ProfesorController
 * Fecha de creación: 20/09/2022 , 18:00
 * Autor: Lloyd Castillo Ramos
 * Descripción: Clase que maneja el controlador de la clase profesor
 */
@RequestMapping("/api/v1/")
@RestController
public class ProfesorController {
    private final ProfesorRepository profesorRepository;
    private final CursoRepository cursoRepository;
    private final UsuarioRepository usuarioRepository;

    private final RolRepository rolRepository;
    private final static Logger log = LoggerFactory.getLogger(ProfesorController.class);

    public ProfesorController(ProfesorRepository profesorRepository,
                              UsuarioRepository usuarioRepository,
                              CursoRepository cursoRepository,RolRepository rolRepository) {
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.rolRepository = rolRepository;
    }

    @GetMapping("/profesor/{idUsuario}/{idCurso}")
    public Profesor getProfesorFromIdUsuarioAndIdCurso(@PathVariable int idUsuario, @PathVariable int idCurso) {
        log.info("OBTENER PROFESOR");
        var usr = usuarioRepository.findById(idUsuario).orElseThrow();
        var curso = cursoRepository.findByIdCursoAndActivoIsTrue(idCurso).orElseThrow();
        return profesorRepository.queryByUsuarioAndCurso(usr, curso).orElseThrow();
    }

    @GetMapping("profesor/listarSinAsignar/{idCurso}/{idEspecialidad}")
    public Set<Usuario> listarSinAsignarUsuario(@PathVariable int idCurso,@PathVariable int idEspecialidad){
        Set<Usuario> listaUsuarios = new HashSet<>();
        var profesores = profesorRepository.queryAllByCursoIsNull(idCurso,idEspecialidad);
        var rol = rolRepository.queryByNombre("COORDINADOR").orElseThrow();
        var usuarios = usuarioRepository.queryUsersWithSingleRol(rol);
        for(Profesor pr: profesores) {
            listaUsuarios.add(pr.getUsuario());
        }
        listaUsuarios.addAll(usuarios);
        return listaUsuarios;
    }
}
