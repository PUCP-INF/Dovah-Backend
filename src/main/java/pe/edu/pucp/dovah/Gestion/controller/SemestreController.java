package pe.edu.pucp.dovah.Gestion.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pe.edu.pucp.dovah.Gestion.exception.SemestreNotFoundException;
import pe.edu.pucp.dovah.Gestion.model.Semestre;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.Gestion.repository.SemestreRepository;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/")
@RestController
public class SemestreController {

    private final SemestreRepository semestreRepository;
    private final CursoRepository cursoRepository;
    private final static Logger log = LoggerFactory.getLogger(SemestreController.class);

    public SemestreController(SemestreRepository semestreRepository, CursoRepository cursoRepository) {
        this.semestreRepository = semestreRepository;
        this.cursoRepository = cursoRepository;
    }

    /*Listar todos los semestres activos*/
    @GetMapping("/semestre")
    List<Semestre> all(){

        return semestreRepository.queryAllByActivoIsTrue();

    }

    /*Buscar un semestre*/
    @GetMapping("/semestre/{id}")
    Semestre obtenerSemestrePorId(@PathVariable int id){
        return semestreRepository.findByIdSemestreAndActivoIsTrue(id).orElseThrow(() -> new SemestreNotFoundException(id));
    }

    /*Insertar un semestre*/
    @PostMapping("/semestre")
    Semestre nuevoSemestre(@RequestBody Map<String,Object> nuevoSemestre){
        log.info("Agregando semestre");
        var json = new JSONObject(nuevoSemestre);
        var semestre = new Semestre(json.getString("anhoAcademico"),json.getString("periodo"),
                json.getString("fechaInicio"),json.getString("fechaFin"));

        return semestreRepository.save(semestre);
    }

    /*Eliminar un semestre*/
    @PostMapping("/semestre/eliminar")
    Semestre eliminarSemestre(@RequestBody Map<String, Object> map){

        var json = new JSONObject(map);
        int id = json.getInt("idSemestre");
        var semestre = semestreRepository.findByIdSemestreAndActivoIsTrue(id).orElseThrow(()
                -> new SemestreNotFoundException(id));
        log.info(String.format("Eliminando semestre con id '%d'", semestre.getIdSemestre()));
        semestre.setActivo(false);
        return semestreRepository.save(semestre);
    }

    /*Actualizar un semestre*/
    @PostMapping("/semestre/actualizar")
    Semestre actualizarSemestre(@RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        int id = json.getInt("idSemestre");
        String anhoAcademico = json.getString("anhoAcademico");
        String periodo = json.getString("periodo");
        String fechaInicio = json.getString("fechaInicio");
        String fechaFin = json.getString("fechaFin");
        var semestre = semestreRepository.findByIdSemestreAndActivoIsTrue(id).orElseThrow(()
                ->new SemestreNotFoundException(id));
        log.info(String.format("Actualizando atributos de semestre con id '%d'",
                semestre.getIdSemestre()));
        semestre.setAnhoAcademico(anhoAcademico);
        semestre.setPeriodo(periodo);
        semestre.setFechaInicio(fechaInicio);
        semestre.setFechaFin(fechaFin);
        return semestreRepository.save(semestre);
    }

    @GetMapping("/semestre/getAnterior/{idSemestre}")
    public Semestre getSemestrePasado(@PathVariable Integer idSemestre) {
        var sems = semestreRepository.queryAllActivos();
        if (sems.size() <= 1) return null;
        var last = sems.get(sems.size()-1);
        var it = sems.listIterator();
        Semestre prev = null;
        Semestre cur;
        if (it.hasNext()) prev = it.next();
        while (it.hasNext()) {
            cur = it.next();
            if (cur.getIdSemestre() == idSemestre) {
                break;
            }
            prev = cur;
        }
        if (prev == last) return null;
        return prev;
    }
}
