/*
 * Nombre del archivo: FacultadController.java
 * Fecha de creacion: 21-09-2022
 * Autor: Victor Avalos
 * Descripción: Definición de los metodos usados para la clase Facultad
 */
package pe.edu.pucp.dovah.Gestion.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.exception.EspecialidadNotFoundException;
import pe.edu.pucp.dovah.Gestion.exception.FacultadNotFoundException;
import pe.edu.pucp.dovah.Gestion.model.Facultad;
import pe.edu.pucp.dovah.Gestion.repository.EspecialidadRepository;
import pe.edu.pucp.dovah.Gestion.repository.FacultadRepository;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/")
@RestController
public class FacultadController {

    private final FacultadRepository repository;
    private final EspecialidadRepository repositoryEsp;
    private final static Logger log = LoggerFactory.getLogger(FacultadController.class);

    public FacultadController(FacultadRepository repositoryFac, EspecialidadRepository repositoryEsp) {

        this.repository = repositoryFac;
        this.repositoryEsp = repositoryEsp;

    }

    /*Listar todas las facultades*/
    @GetMapping("/facultad")
    List<Facultad> all(){

        return repository.queryAllByActivoIsTrue();

    }

    /*Buscar una facultad*/
    @GetMapping("/facultad/{id}")
    Facultad obtenerFacultadPorId(@PathVariable int id){
        return repository.findByIdFacultadAndActivoIsTrue(id).orElseThrow(() -> new FacultadNotFoundException(id));
    }

    /*Eliminar una facultad*/
    @PostMapping("/facultad/eliminar")
    Facultad eliminarFacultad(@RequestBody Map<String, Object> map){

        var json = new JSONObject(map);
        int id = json.getInt("idFacultad");
        var facultad = repository.findByIdFacultadAndActivoIsTrue(id).orElseThrow(()
                -> new FacultadNotFoundException(id));
        log.info(String.format("Eliminando facultad con id '%d'", facultad.getIdFacultad()));
        facultad.setActivo(false);
        return repository.save(facultad);
    }

    /*Insertar una facultad*/
    @PostMapping("/facultad")
    Facultad nuevaFacultad(@RequestBody Map<String,Object> nuevaFacultad){
        log.info("Agregando facultad");
        var json = new JSONObject(nuevaFacultad);
        var facultad = new Facultad(json.getString("nombre"));
        return repository.save(facultad);
    }

    /*Agregar una especialidad a una facultad*/
    @PostMapping("/facultad/agregarEspecialidad")
    Facultad agregarEspecialidad(@RequestBody Map<String, Object> map){

        var json = new JSONObject(map);
        int idFacultad = json.getInt("idFacultad");
        int idEspecialidad = json.getInt("idEspecialidad");
        var facultad = repository.findByIdFacultadAndActivoIsTrue(idFacultad).orElseThrow(()->
                                new FacultadNotFoundException(idFacultad));
        var esp = repositoryEsp.findByIdEspecialidadAndActivoIsTrue(idEspecialidad)
                                .orElseThrow(()-> new EspecialidadNotFoundException(idEspecialidad));
        facultad.getEspecialidades().add(esp);
        esp.setFacultad(facultad);
        repositoryEsp.save(esp);
        return repository.save(facultad);
    }

    /*Actualizar una facultad*/
    @PostMapping("/facultad/actualizar")
    Facultad actualizarFacultad(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        int id = json.getInt("idFacultad");
        String nombre = json.getString("nombre");
        var facultad = repository.findByIdFacultadAndActivoIsTrue(id).orElseThrow(()
                ->new FacultadNotFoundException(id));
        log.info(String.format("Actualizando atributos de facultad con id '%d'",
                facultad.getIdFacultad()));
        facultad.setNombre(nombre);
        return repository.save(facultad);
    }

}
