package pe.edu.pucp.dovah.mensajes.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.pucp.dovah.mensajes.model.Hilo;
import pe.edu.pucp.dovah.mensajes.repository.HiloRepository;

import java.util.UUID;

@RequestMapping("/api/v1")
@RestController
public class HiloController {
    private final HiloRepository hiloRepository;

    public HiloController(HiloRepository hiloRepository) {
        this.hiloRepository = hiloRepository;
    }

    @GetMapping("/hilo/{uuid}")
    public Hilo getHiloWithComments(@PathVariable UUID uuid) {
        return hiloRepository.queryByUuidWithComentarios(uuid).orElseThrow();
    }
}
