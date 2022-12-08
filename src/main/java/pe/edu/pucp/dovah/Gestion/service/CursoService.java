package pe.edu.pucp.dovah.Gestion.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;
import pe.edu.pucp.dovah.Gestion.model.Curso;
import pe.edu.pucp.dovah.Gestion.repository.CursoRepository;
import pe.edu.pucp.dovah.PlanTesis.model.ProfesorInscritoPlanTesis;
import pe.edu.pucp.dovah.PlanTesis.repository.ProfesorInscritoPlanTesisRepository;
import pe.edu.pucp.dovah.RRHH.model.Alumno;
import pe.edu.pucp.dovah.RRHH.model.Profesor;
import pe.edu.pucp.dovah.RRHH.model.Usuario;
import pe.edu.pucp.dovah.RRHH.repository.AlumnoRepository;
import pe.edu.pucp.dovah.RRHH.repository.ProfesorRepository;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.asignaciones.exception.DocumentoNotFoundException;
import pe.edu.pucp.dovah.asignaciones.repository.DocumentoRepository;

import java.util.*;

@Service
public class CursoService {
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;
    private final CursoRepository cursoRepository;
    private final DocumentoRepository documentoRepository;
    private final ProfesorInscritoPlanTesisRepository profesorInscritoPlanTesisRepository;

    public CursoService(AlumnoRepository alumnoRepository,
                        ProfesorRepository profesorRepository,
                        CursoRepository cursoRepository,
                        DocumentoRepository documentoRepository,
                        ProfesorInscritoPlanTesisRepository profesorInscritoPlanTesisRepository) {
        this.alumnoRepository = alumnoRepository;
        this.profesorRepository = profesorRepository;
        this.cursoRepository = cursoRepository;
        this.documentoRepository = documentoRepository;
        this.profesorInscritoPlanTesisRepository = profesorInscritoPlanTesisRepository;
    }

    public void addAlumnoProfesorToCurso(Usuario usr, Curso curso, String tipo, Set<Rol> roles) {
        if (Objects.equals(tipo, "profesor")) {
            var alumn = alumnoRepository.queryByUsuarioAndCurso(usr, curso);
            if (alumn.isPresent()) return;
            Profesor prof;
            var tmp = profesorRepository.queryByUsuarioAndCurso(usr, curso);
            if (tmp.isPresent()) {
                prof = tmp.get();
                prof.setActivo(true);
                profesorRepository.save(prof);
                return;
            }
            prof = new Profesor();
            prof.setUsuario(usr);
            prof.setCurso(curso);
            prof.setRoles(roles);
            profesorRepository.save(prof);
        } else if (Objects.equals(tipo, "alumno")) {
            var prof = profesorRepository.queryByUsuarioAndCurso(usr, curso);
            if (prof.isPresent()) return;
            Alumno alumn;
            var tmp = alumnoRepository.queryByUsuarioAndCurso(usr, curso);
            if (tmp.isPresent()) {
                alumn = tmp.get();
                alumn.setActivo(true);
                alumnoRepository.save(alumn);
                return;
            }
            alumn = new Alumno();
            alumn.setUsuario(usr);
            if (curso.getCursoRequisito() != null) {
                var oldAlumn = alumnoRepository
                        .queryByUsuarioAndCurso(alumn.getUsuario(), curso.getCursoRequisito());
                oldAlumn.ifPresent(alumno -> alumn.setPlanTesis(alumno.getPlanTesis()));
            }
            alumn.setCurso(curso);
            alumnoRepository.save(alumn);
        }
    }

    public Curso agregarEliminarDocumento(JSONObject json, String tipoAccion) {
        int idCurso = json.getInt("idCurso");
        long idDocumento = json.getLong("idDocumento");
        var curso = cursoRepository.queryCursoWithDocumentos(idCurso).orElseThrow();
        var documento = documentoRepository.queryByIdAndActivoIsTrue(idDocumento).orElseThrow(()->
                new DocumentoNotFoundException(idDocumento));
        if (Objects.equals(tipoAccion, "eliminar")) {
            curso.getDocumentosGenerales().remove(documento);
        } else if (Objects.equals(tipoAccion, "agregar")){
            curso.getDocumentosGenerales().add(documento);
        }
        return cursoRepository.save(curso);
    }

    public void obtenerDatosDeCursoRequisito(Curso curso) {
        // jalarnos profesores
        var cursoReq = cursoRepository
                .queryByIdCursoWithProfesores(curso.getCursoRequisito().getIdCurso()).orElseThrow();
        var newProfs = new ArrayList<Profesor>();
        for (var prof: cursoReq.getProfesores()) {
            var newProf = new Profesor();
            newProf.setRoles(Set.copyOf(prof.getRoles()));
            newProf.setUsuario(prof.getUsuario());
            newProf.setCurso(curso);
            profesorRepository.save(newProf);
            newProfs.add(newProf);
        }
        var inscSet = profesorInscritoPlanTesisRepository.queryByCursoWithRoles(cursoReq);
        for (var prof: newProfs) {
            for (var insc: inscSet) {
                if (prof.getUsuario().equals(insc.getProfesor().getUsuario())) {
                    var newInsc = new ProfesorInscritoPlanTesis();
                    newInsc.setProfesor(prof);
                    newInsc.setPlanTesis(insc.getPlanTesis());
                    newInsc.setRoles(Set.copyOf(insc.getRoles()));
                    profesorInscritoPlanTesisRepository.save(newInsc);
                }
            }
        }
    }
}
