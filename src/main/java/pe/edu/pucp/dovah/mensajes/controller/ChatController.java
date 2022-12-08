package pe.edu.pucp.dovah.mensajes.controller;

import org.json.JSONObject;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import pe.edu.pucp.dovah.RRHH.repository.UsuarioRepository;
import pe.edu.pucp.dovah.asignaciones.repository.DocumentoRepository;
import pe.edu.pucp.dovah.mensajes.model.Comentario;
import pe.edu.pucp.dovah.mensajes.repository.ComentarioRepository;
import pe.edu.pucp.dovah.mensajes.repository.HiloRepository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
public class ChatController {
    private final UsuarioRepository usuarioRepository;
    private final HiloRepository hiloRepository;
    private final ComentarioRepository comentarioRepository;
    private final DocumentoRepository documentoRepository;

    public ChatController(UsuarioRepository usuarioRepository,
                          HiloRepository hiloRepository,
                          ComentarioRepository comentarioRepository,
                          DocumentoRepository documentoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.hiloRepository = hiloRepository;
        this.comentarioRepository = comentarioRepository;
        this.documentoRepository = documentoRepository;
    }

    @MessageMapping("/chat/{chatId}")
    public Comentario agregarComentario(@DestinationVariable String chatId, @RequestBody Map<String, Object> map) {
        var json = new JSONObject(map);
        var com = new Comentario();
        var usr = usuarioRepository.findById(json.getInt("idUsuario")).orElseThrow();
        var hilo = hiloRepository.queryByUuidWithComentarios(UUID.fromString(chatId)).orElseThrow();
        com.setMensaje(json.getString("mensaje"));
        com.setUsuario(usr);
        com.setHilo(hilo);
        return comentarioRepository.save(com);
    }

    @MessageMapping("/chat/doc/{chatId}")
    public Comentario agregarDocumento(@DestinationVariable String chatId, @RequestBody Map<String, Object> map) throws IOException {
        var json = new JSONObject(map);
        var com = new Comentario();
        var usr = usuarioRepository.findById(json.getInt("idUsuario")).orElseThrow();
        var hilo = hiloRepository.queryByUuidWithComentarios(UUID.fromString(chatId)).orElseThrow();
        var doc = documentoRepository.queryByIdAndActivoIsTrue(json.getLong("idDocumento")).orElseThrow();
        com.setUsuario(usr);
        com.setHilo(hilo);
        com.setDocumento(doc);
        return comentarioRepository.save(com);
    }
}
