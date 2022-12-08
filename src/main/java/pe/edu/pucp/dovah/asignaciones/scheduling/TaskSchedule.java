/*
 * Nombre del archivo: TaskSchedule
 * Fecha de creación: 26/11/2022 , 06:40
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.asignaciones.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pe.edu.pucp.dovah.asignaciones.controller.TareaController;
import pe.edu.pucp.dovah.asignaciones.model.EstadoEntrega;
import pe.edu.pucp.dovah.asignaciones.model.Tarea;
import pe.edu.pucp.dovah.asignaciones.model.TareaEntrega;
import pe.edu.pucp.dovah.asignaciones.repository.TareaEntregaRepository;
import pe.edu.pucp.dovah.asignaciones.repository.TareaRepository;

import java.time.LocalDateTime;


@Component
public class TaskSchedule {
    private final long segundo = 1000;
    private final long minuto = segundo*60;
    private final long hora = minuto*60;

    private final TareaRepository tareaRepository;
    private final TareaEntregaRepository tareaEntregaRepository;

    private final static Logger log = LoggerFactory.getLogger(TareaController.class);

    public TaskSchedule(TareaRepository tareaRepository, TareaEntregaRepository tareaEntregaRepository) {
        this.tareaRepository = tareaRepository;
        this.tareaEntregaRepository = tareaEntregaRepository;
    }

    @Scheduled(fixedDelay = hora)
    public void verificarPorHora(){
        log.info("Verificando cada minuto");
        var tareas = tareaRepository.queryAllTareasByFechaFinalizada(LocalDateTime.now());
        for(Tarea tr: tareas){
            for(TareaEntrega te: tr.getTareaEntregas()){
                te.setEstadoEntrega(EstadoEntrega.FINALIZADA);
                tareaEntregaRepository.save(te);
            }
        }
    }

}
