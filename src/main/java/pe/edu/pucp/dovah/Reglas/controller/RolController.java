/*
 * Nombre del archivo: RolController
 * Fecha de creación: 1/10/2022 , 08:30
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.Reglas.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.Reglas.exception.RolNotFoundException;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/")
@RestController
public class RolController {

    private final RolRepository repositoryRol;
    private final static Logger log = LoggerFactory.getLogger(RolController.class);
    public RolController(RolRepository repository,UsuarioRepository usuarioRepository) {

        this.repositoryRol = repository;

    }
    /*

      Listar todos los roles

  */
    @GetMapping("/rol")
    List<Rol> all(){
        return repositoryRol.findAll();

    }
    /*

       Listar un rol en especifico

   */
    @GetMapping("/rol/{id}")
    Rol obtenerRolPorId(@PathVariable int id){

        return repositoryRol.findById(id).orElseThrow(() -> new RolNotFoundException(id));

    }

    @PostMapping("/rol")
    Rol nuevoRol(@RequestBody Map<String,Object> nuevoRol){

        log.info("Agregando Rol");
        var json = new JSONObject(nuevoRol);
        var rol = new Rol(json.getString("nombre"));
        return repositoryRol.save(rol);

    }
}
