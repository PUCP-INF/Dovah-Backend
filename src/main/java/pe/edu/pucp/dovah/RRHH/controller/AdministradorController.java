package pe.edu.pucp.dovah.RRHH.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.repository.EspecialidadRepository;
import pe.edu.pucp.dovah.Gestion.repository.FacultadRepository;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.RRHH.service.UsuarioService;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;

import java.util.List;
import java.util.Map;


/*
 * Nombre del archivo: AdmnistradorController
 * Fecha de creación: 20/09/2022 , 18:00
 * Autor: Lloyd Castillo Ramos
 * Descripción: Clase que maneja el controlador de la clase administrador
 */
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/v1/admin")
@RestController
public class AdministradorController {
    private final static Logger log = LoggerFactory.getLogger(AdministradorController.class);
    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final FacultadRepository facultadRepository;
    private final EspecialidadRepository especialidadRepository;

    public AdministradorController(UsuarioService usuarioService,
                                   UsuarioRepository usuarioRepository,
                                   FacultadRepository facultadRepository,
                                   EspecialidadRepository especialidadRepository,
                                   RolRepository rolRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.facultadRepository = facultadRepository;
        this.especialidadRepository = especialidadRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/listar/coordinador")
    public List<Usuario> listarCoordinadores() {
        log.info("LISTAR COORDINADOR");
        var rol = rolRepository.queryByNombre("COORDINADOR").orElseThrow();
        return usuarioRepository.queryUsersWithRol(rol);
    }

    @PostMapping("/nuevo/coordinador")
    public Usuario insertarCoordinador(@RequestBody Map<String, Object> map) {
        log.info("INSERTAR COORDINADOR");
        var json = new JSONObject(map);
        var usr = new Usuario(json.getString("nombre"),json.getString("apellido"),
                json.getString("codigoPUCP"),
                json.getString("correo"),json.getString("password"));
        var esp = especialidadRepository.findByIdEspecialidadAndActivoIsTrue(json.getInt("idEspecialidad")).orElseThrow();
        var fac = facultadRepository.findByIdFacultadAndActivoIsTrue(json.getInt("idFacultad")).orElseThrow();
        usr = usuarioService.newUser(usr);
        usr.setEspecialidad(esp);
        usr.setFacultad(fac);
        usr.getListaRoles().add(rolRepository.queryByNombre("COORDINADOR").orElseThrow());
        return usuarioRepository.save(usr);
    }

    @PutMapping("/modificar/coordinador")
    public Usuario modificarCoordinador(@RequestBody Map<String, Object> map) {
        log.info("MODIFICAR COORDINADOR");
        var json = new JSONObject(map);
        return usuarioService.modifyUser(json);
    }

    @DeleteMapping("/eliminar/coordinador/{idUsuario}")
    public void eliminarCoordinador(@PathVariable int idUsuario) {
        log.info("ELIMINAR COORDINADOR");
        var rol = rolRepository.queryByNombre("COORDINADOR").orElseThrow();
        var usr = usuarioRepository.queryByIdWithRoles(idUsuario).orElseThrow();
        usr.getListaRoles().remove(rol);
        usuarioRepository.save(usr);
    }
}
