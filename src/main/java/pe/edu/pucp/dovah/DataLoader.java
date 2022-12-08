package pe.edu.pucp.dovah;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import pe.edu.pucp.dovah.Gestion.model.*;
import pe.edu.pucp.dovah.Gestion.repository.*;
import pe.edu.pucp.dovah.PlanTesis.model.AreaPlanTesis;
import pe.edu.pucp.dovah.PlanTesis.model.PeriodoPlanTesis;
import pe.edu.pucp.dovah.PlanTesis.model.PlanTesis;
import pe.edu.pucp.dovah.PlanTesis.repository.AreaPlanTesisRepository;
import pe.edu.pucp.dovah.PlanTesis.repository.PeriodoPlanTesisRepository;
import pe.edu.pucp.dovah.PlanTesis.repository.PlanTesisRepository;
import pe.edu.pucp.dovah.RRHH.model.*;
import pe.edu.pucp.dovah.RRHH.repository.*;
import pe.edu.pucp.dovah.RRHH.service.UsuarioService;
import pe.edu.pucp.dovah.Reglas.model.Rol;
import pe.edu.pucp.dovah.Reglas.repository.RolRepository;
import pe.edu.pucp.dovah.asignaciones.model.Tarea;
import pe.edu.pucp.dovah.asignaciones.repository.TareaRepository;
import pe.edu.pucp.dovah.mensajes.model.Hilo;
import pe.edu.pucp.dovah.mensajes.repository.HiloRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DataLoader implements ApplicationRunner {
    private final static Logger log = LoggerFactory.getLogger(DataLoader.class);
    private final FacultadRepository facultadRepository;
    private final EspecialidadRepository especialidadRepository;
    private final SemestreRepository semestreRepository;
    private final CursoRepository cursoRepository;
    private final TareaRepository tareaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PlanTesisRepository planTesisRepository;
    private final AreaPlanTesisRepository areaPlanTesisRepository;
    private final PeriodoPlanTesisRepository periodoPlanTesisRepository;
    private final HiloRepository hiloRepository;
    private final UsuarioService usuarioService;

    private Facultad fac;
    private Especialidad esp;
    private AreaPlanTesis apt;
    private Semestre s20231;
    private Curso curso;
    private PeriodoPlanTesis periodo;

    public DataLoader(FacultadRepository facultadRepository,
                      EspecialidadRepository especialidadRepository,
                      SemestreRepository semestreRepository,
                      CursoRepository cursoRepository,
                      TareaRepository tareaRepository,
                      UsuarioRepository usuarioRepository,
                      RolRepository rolRepository,
                      PlanTesisRepository planTesisRepository,
                      AreaPlanTesisRepository areaPlanTesisRepository,
                      HiloRepository hiloRepository,
                      PeriodoPlanTesisRepository periodoPlanTesisRepository,
                      UsuarioService usuarioService) {
        this.facultadRepository = facultadRepository;
        this.especialidadRepository = especialidadRepository;
        this.semestreRepository = semestreRepository;
        this.cursoRepository = cursoRepository;
        this.tareaRepository = tareaRepository;
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.planTesisRepository = planTesisRepository;
        this.areaPlanTesisRepository = areaPlanTesisRepository;
        this.hiloRepository = hiloRepository;
        this.periodoPlanTesisRepository = periodoPlanTesisRepository;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(ApplicationArguments args) {
        insertarRoles();
        insertarAdmin();
        insertarFacultades();
        insertarEspecialidades();
        insertarSemestres();
        insertarCoordinador();
        insertarCursos();
        insertarTareas();
        insertarAreaPlanTesis();
        insertarPeriodoTesis();
        insertarPlanTesis();
    }

    private void insertarFacultades() {
        if (!facultadRepository.findAll().isEmpty()) return;
        fac = facultadRepository.save(new Facultad("Ciencias e Ingenieria"));
        log.info("Agregando Facultad..."+fac);
        log.info("Agregando Facultad..."+facultadRepository.save(new Facultad("Arquitectura y Urbanismo")));
    }

    private void insertarEspecialidades() {
        if (!especialidadRepository.findAll().isEmpty()) return;
        esp = especialidadRepository.save(new Especialidad("Ingenieria Informatica", "INF",fac));
        log.info("Agregando Especialidad..." + esp);
        log.info("Agregando Especialidad..."+
                especialidadRepository.save(new Especialidad("Ingenieria Industrial", "IND", fac)));
    }

    private void insertarSemestres() {
        if (!semestreRepository.findAll().isEmpty()) return;
        s20231 = new Semestre("2023",
                "1","15/03/2023","15/07/2023");
        log.info("Agregando Semestre..."+semestreRepository.save(s20231));
        var s20232 = new Semestre("2023",
                "2","15/08/2023","10/12/2023");
        log.info("Agregando Semestre..."+semestreRepository.save(s20232));
    }

    private void insertarCursos() {
        if (!cursoRepository.findAll().isEmpty()) return;
        curso = new Curso("INF282","Proyecto de Tesis I", esp, s20231);
        log.info("Agregando cursos..."+cursoRepository.save(curso));
        var curso = new Curso("INF262","Proyecto de Tesis II", esp, s20231);
        log.info("Agregando cursos..."+cursoRepository.save(curso));
    }

    private void insertarTareas() {
        if (!tareaRepository.findAll().isEmpty()) return;
        var now = LocalDateTime.now();
        now = now.plusDays(1);
        var tarea = new Tarea("Entregable 1", "Entregable", now);
        tarea.setCurso(curso);
        log.info("Preloading " + tareaRepository.save(tarea));
        now = now.plusDays(1);
        tarea = new Tarea("Entregable 2", "Entregable", now);
        tarea.setCurso(curso);
        log.info("Preloading " + tareaRepository.save(tarea));
        now = now.plusDays(1);
        tarea = new Tarea("Entregable 3", "Entregable", now);
        tarea.setCurso(curso);
        log.info("Preloading " + tareaRepository.save(tarea));
        now = now.plusDays(1);
        tarea = new Tarea("Entregable 4", "Entregable", now);
        tarea.setCurso(curso);
        log.info("Preloading " + tareaRepository.save(tarea));
        now = now.plusDays(1);
        tarea = new Tarea("Exposicion 1", "Exposicion", now);
        tarea.setCurso(curso);
        tarea.setEsExposicion(true);
        log.info("Preloading " + tareaRepository.save(tarea));
        now = now.plusDays(1);
        tarea = new Tarea("Exposicion 2", "Exposicion", now);
        tarea.setCurso(curso);
        tarea.setEsExposicion(true);
        log.info("Preloading " + tareaRepository.save(tarea));
    }

    private void insertarRoles() {
        if (!rolRepository.findAll().isEmpty()) return;
        log.info("Agregando Roles..."+rolRepository.save(new Rol("ADMIN")));
        log.info("Agregando Roles..."+rolRepository.save(new Rol("ALUMNO")));
        log.info("Agregando Roles..."+rolRepository.save(new Rol("ASESOR")));
        log.info("Agregando Roles..."+rolRepository.save(new Rol("JURADO")));
        log.info("Agregando Roles..."+rolRepository.save(new Rol("PROFESOR")));
        log.info("Agregando Roles..."+rolRepository.save(new Rol("COORDINADOR")));
    }

    private void insertarAdmin() {
        var rol = rolRepository.queryByNombre("ADMIN").orElseThrow();
        var admin = new Usuario("csantve@gmail.com", "ctorov", rol);
        admin.setNombre("Carlos");
        admin.setSexo('M');
        admin.setApellido("Toro");
        admin.setCodigoPUCP("2017878");
        log.info("Creando administrador " + usuarioService.newSimpleUser(admin));
    }

    private void insertarCoordinador() {
        var rol = rolRepository.queryByNombre("COORDINADOR").orElseThrow();
        var usuario = new Usuario("rcueva@pucp.edu.pe", "rcueva23", fac, esp);
        usuario.setNombre("Rony");
        usuario.setSexo('M');
        usuario.setApellido("Cueva");
        usuario.setCodigoPUCP("58945342");
        usuario.getListaRoles().add(rol);
        log.info("Creando usuario " + usuarioService.newSimpleUser(usuario));
    }

    private void insertarAreaPlanTesis() {
        if (!areaPlanTesisRepository.findAll().isEmpty()) return;
        apt = new AreaPlanTesis("Ciencias de la Computación",esp);
        log.info("Insertando area de especialidad de tesis " + areaPlanTesisRepository.save(apt));
        var area = new AreaPlanTesis("Tecnologías de Información",esp);
        log.info("Insertando area de especialidad de tesis " + areaPlanTesisRepository.save(area));
        area = new AreaPlanTesis("Ingeniería de Software",esp);
        log.info("Insertando area de especialidad de tesis " + areaPlanTesisRepository.save(area));
        area = new AreaPlanTesis("Sistemas de Información",esp);
        log.info("Insertando area de especialidad de tesis " + areaPlanTesisRepository.save(area));
    }

    private void insertarPeriodoTesis() {
        if (!periodoPlanTesisRepository.findAll().isEmpty()) return;
        periodo = new PeriodoPlanTesis();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        // inicio de 2022
        var ini = LocalDate.parse("01-01-2022", formatter);
        // fines de 2023
        var fin = LocalDate.parse("01-01-2024", formatter);
        periodo.setFechaInicio(ini);
        periodo.setFechaFin(fin);
        periodoPlanTesisRepository.save(periodo);
    }

    private void insertarPlanTesis() {
        if (!planTesisRepository.findAll().isEmpty()) return;
        var pt = new PlanTesis("Chatbot conversacional que sugiere libros según las preferencias del usuario",
                apt,
                "Estos chatbots ofrecen al usuario la posibilidad de elegir " +
                        "entre varias opciones presentadas en forma de menús o botones. " +
                        "En función de lo que el usuario pulse, el bot le proporciona otra " +
                        "serie de opciones para elegir, llegando a la sugerencia idónea para satisfacer al usuario.",
                fac);
        var usr = usuarioRepository.queryByCorreoWithRoles("rcueva@pucp.edu.pe").orElseThrow();
        pt.setProponiente(usr);
        pt.setDetallesAdicionales("Es necesario tener craest de 80 para hacer esta tesis.");
        Hilo hilo = new Hilo();
        hiloRepository.save(hilo);
        pt.setHilo(hilo);
        pt.setPeriodoPlanTesis(periodo);
        log.info("Insertando " + planTesisRepository.save(pt));
    }
}
