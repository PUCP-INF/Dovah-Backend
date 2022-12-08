/*
 * Nombre del archivo: UsuarioController
 * Fecha de creación: 1/10/2022 , 09:02
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.RRHH.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.RRHH.exceptions.UsuarioNotFoundException;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.RRHH.service.UsuarioService;
import pe.edu.pucp.dovah.Reglas.exception.RolNotFoundException;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;

import java.util.*;


@RequestMapping("/api/v1/")
@RestController
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final CursoRepository cursoRepository;
    private final static Logger log = LoggerFactory.getLogger(UsuarioController.class);

    public UsuarioController(UsuarioRepository repository,
                             UsuarioService usuarioService,
                             CursoRepository cursoRepository,
                             RolRepository rolRepository) {
        this.usuarioRepository = repository;
        this.rolRepository = rolRepository;
        this.usuarioService = usuarioService;
        this.cursoRepository = cursoRepository;
    }

    @GetMapping("/usuario")
    List<Usuario>listarTodos(){
        return usuarioRepository.queryAllWithRoles();
    }

    @GetMapping("/usuario/especialidad/{idEspecialidad}")
    public List<Usuario> getUsersByEspecialidad(@PathVariable int idEspecialidad) {
        return usuarioRepository.queryAllByEspecialidad(idEspecialidad);
    }

    @GetMapping("/usuario/especialidad/{idEspecialidad}/roles/exist")
    public List<Usuario> getUsersByEspecialidadRoleExist(@PathVariable int idEspecialidad) {
        return usuarioRepository.queryAllByEspecialidadRolesExist(idEspecialidad);
    }

    @GetMapping("/usuario/especialidad/{idEspecialidad}/in/curso/{idCurso}")
    public List<Usuario> getUsersInCurso(@PathVariable int idEspecialidad, @PathVariable int idCurso) {
        var usrs = new ArrayList<Usuario>();
        try {
            var res = cursoRepository.queryByIdCursoWithAlumnos(idCurso).orElseThrow().getAlumnos();
            for (var alumn: res) usrs.add(alumn.getUsuario());

        } catch (NoSuchElementException ignore) {}
        return usrs;
    }

    @PostMapping("/usuario/auth")
    public Map<String, Object> loginHandler(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        String correo = json.getString("correo");
        String password = json.getString("password");
        Map<String, Object> res = Collections.singletonMap("jwt-token", "");
        try {
            res = usuarioService.authenticateUser(correo, password);
        } catch (RuntimeException ignore) {}
        return res;
    }

    @GetMapping("/usuario/auth/info")
    public Usuario getLoggedInUserDetails() {
        return usuarioService.getLoggedInUser();
    }

    @GetMapping("/usuario/{id}")
    Usuario obtenerUsuarioPorId(@PathVariable int id){

        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNotFoundException(id));

    }

    @PostMapping("/usuario/agregarRol")
    Usuario agregarRol(@RequestBody Map<String, Object> map){

        var json = new JSONObject(map);
        int idUsuario = json.getInt("idUsuario");
        int idRol = json.getInt("idRol");
        var usuario = usuarioRepository.queryAllByIdUsuario(idUsuario).orElseThrow(()->
                new UsuarioNotFoundException(idUsuario));
        var rol = rolRepository.findById(idRol).orElseThrow(()->new RolNotFoundException(idRol));
        usuario.getListaRoles().add(rol);
        return usuarioRepository.save(usuario);

    }
    /*Agregar Usuario*/
    @PostMapping("/usuario")
    Usuario nuevoUsuario(@RequestBody Map<String,Object> nuevoUsuario){
        log.info("Agregando usuario");
        var json = new JSONObject(nuevoUsuario);
        var usr = new Usuario(json.getString("nombre"),json.getString("apellido"),
                json.getString("codigoPUCP"),
                json.getString("correo"),json.getString("password"));
        return usuarioService.newUser(usr);
    }

    /*Modificar Usuario*/
    @PostMapping("/usuario/modificar")
    Usuario modificarUsuario(@RequestBody Map<String, Object>map){
        var json = new JSONObject(map);
        return usuarioService.modifyUser(json);
    }

    /*Eliminar Usuario*/
    @PostMapping("usuario/eliminar")
    Usuario eliminarUsuario(@RequestBody Map<String, Object>map){

        var json = new JSONObject(map);
        int id = json.getInt("idUsuario");
        var usuario = usuarioRepository.findById(id).orElseThrow(()->new UsuarioNotFoundException(id));
        log.info(String.format("Eliminando usuario con id '%d'",usuario.getIdUsuario()));
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }

    @PostMapping("usuario/eliminarRol")
    Usuario eliminarRolUsuario(@RequestBody Map<String,Object>map){

        var json = new JSONObject(map);
        int id = json.getInt("idUsuario");
        int idRol = json.getInt("idRol");
        var usuario = usuarioRepository.findById(id).orElseThrow(()->new UsuarioNotFoundException(id));
        var rol = rolRepository.findById(idRol).orElseThrow(()-> new RolNotFoundException(idRol));
        log.info(String.format("Eliminando rol de usuario '%d'",usuario.getIdUsuario()));
        for(Rol roles : usuario.getListaRoles()){
            if(roles.getIdRol() == rol.getIdRol()){
                usuario.getListaRoles().remove(roles);
                break;
            }
        }
        return usuarioRepository.save(usuario);
    }

}
