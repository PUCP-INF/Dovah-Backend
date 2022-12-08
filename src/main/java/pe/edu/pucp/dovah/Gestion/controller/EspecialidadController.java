/*
 * Nombre del archivo: EspecialidadController.java
 * Fecha de creacion: 21-09-2022
 * Autor: Victor Avalos
 * Descripción: Definición de los metodos usados para la clase Especialidad
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
import pe.edu.pucp.dovah.Gestion.model.Especialidad;
import pe.edu.pucp.dovah.Gestion.model.Facultad;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.Gestion.repository.EspecialidadRepository;
import pe.edu.pucp.dovah.Gestion.repository.FacultadRepository;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/")
@RestController
public class EspecialidadController {

    private final EspecialidadRepository repository;
    private final FacultadRepository facultadRepository;
    private final CursoRepository cursoRepository;

    private final static Logger log = LoggerFactory.getLogger(EspecialidadController.class);

    public EspecialidadController(EspecialidadRepository repository, FacultadRepository repositoryFacultad,
                                  CursoRepository cursoRepository) {

        this.repository = repository;
        this.facultadRepository = repositoryFacultad;
        this.cursoRepository = cursoRepository;

    }
    /*Listar todos las especialidades*/
    @GetMapping("/especialidad")
    List<Especialidad> all(){
        return repository.queryAllByActivoIsTrue();
    }

    /*Buscar una especialidad*/
    @GetMapping("/especialidad/{id}")
    Especialidad obtenerEspecialidadPorId(@PathVariable int id){
        return repository.findByIdEspecialidadAndActivoIsTrue(id).orElseThrow(()
                -> new EspecialidadNotFoundException(id));
    }

    /*Listar especialidades por facultad*/
    @GetMapping("/especialidadListar/{id}")
    List<Especialidad> listarEspecialidadesPorFacultad(@PathVariable int id){
        List<Especialidad> especialidads =repository.findByFacultad_IdFacultadAndActivoIsTrue(id);
        /*for (Especialidad esp: especialidads){
            if (esp.isActivo())
                especialidads.remove(esp);
        }*/
        return especialidads;
    }

    /*Eliminar una especialidad*/
    @PostMapping("/especialidad/eliminar")
    Especialidad eliminarEspecialidad(@RequestBody Map<String, Object> map){

        var json = new JSONObject(map);
        int idEspecialidad = json.getInt("idEspecialidad");
        var especialidad = repository.findByIdEspecialidadAndActivoIsTrue(idEspecialidad)
                .orElseThrow(()-> new EspecialidadNotFoundException(idEspecialidad));
        log.info(String.format("Eliminando especialidad con id '%d'", especialidad.getIdEspecialidad()));
        especialidad.setActivo(false);
        /*var facultad = facultadRepository.findByIdFacultadAndActivoIsTrue(especialidad.getFacultad().getIdFacultad()).orElseThrow(()
                -> new FacultadNotFoundException(especialidad.getFacultad().getIdFacultad()));
        facultad.setEspecialidades(repository.findByFacultad_IdFacultadAndActivoIsTrue(facultad.getIdFacultad()));
        for (Especialidad esp : facultad.getEspecialidades()){
            if (esp.getIdEspecialidad()==especialidad.getIdEspecialidad()){
                esp.setActivo(false);
                facultadRepository.save(facultad);
                break;
            }
        }
        facultadRepository.save(facultad);*/
        return repository.save(especialidad);
    }

    /*Insertar una especialidad*/

    @PostMapping("/especialidad")
    Especialidad nuevaEspecialidad(@RequestBody Map<String,Object> nuevaEspecialidad){
        log.info("Agregando especialidad");
        var json = new JSONObject(nuevaEspecialidad);
        var especialidad = new Especialidad(json.getString("nombre"),json.getString("codigo"));
        especialidad.setAlumnosProponenTesis(json.getBoolean("alumnosProponenTesis"));
        return repository.save(especialidad);
    }

    @PostMapping("/especialidad/actualizar")
    Especialidad actualizarEspecialidad(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        int idEspecialidad = json.getInt("idEspecialidad");
        String nombre = json.getString("nombre");
        String codigo = json.getString("codigo");
        var especialidad = repository.findByIdEspecialidadAndActivoIsTrue(idEspecialidad).orElseThrow(()
                ->new FacultadNotFoundException(idEspecialidad));
        log.info(String.format("Actualizando atributos de especialidad con id '%d'",
                especialidad.getIdEspecialidad()));
        especialidad.setNombre(nombre);
        especialidad.setCodigo(codigo);
        especialidad.setAlumnosProponenTesis(json.getBoolean("alumnosProponenTesis"));
        return repository.save(especialidad);
    }
}
