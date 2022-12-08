package pe.edu.pucp.dovah.asignaciones.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pe.edu.pucp.dovah.RRHH.service.UsuarioService;
import pe.edu.pucp.dovah.asignaciones.exception.DocumentoNotFoundException;
import pe.edu.pucp.dovah.asignaciones.model.Documento;
import pe.edu.pucp.dovah.asignaciones.repository.DocumentoRepository;
import pe.edu.pucp.dovah.asignaciones.service.S3Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
public class DocumentoController {
    private final static Logger log = LoggerFactory.getLogger(DocumentoController.class);
    private final UsuarioService usuarioService;
    private final DocumentoRepository documentoRepository;
    private final S3Service s3Service;

    public DocumentoController(DocumentoRepository documentoRepository,
                               UsuarioService usuarioService,
                               S3Service s3Service) {
        this.usuarioService = usuarioService;
        this.documentoRepository = documentoRepository;
        this.s3Service = s3Service;
    }

    @GetMapping("/documento")
    List<Documento> getAllDocuments() {
        return documentoRepository.queryAllByActivoIsTrue();
    }

    @GetMapping("/documento/{id}")
    Documento getOneDocument(@PathVariable Long id) {
        return documentoRepository.queryByIdAndActivoIsTrue(id).orElseThrow(() -> new DocumentoNotFoundException(id));
    }

    @GetMapping("/documento/blob/{uuid}/{nombre}")
    ResponseEntity<ByteArrayResource> getDocBlob(@PathVariable String uuid, @PathVariable String nombre) throws IOException, URISyntaxException {
        var doc = documentoRepository.queryByUuidAndNombreAndActivoIsTrue(UUID.fromString(uuid), nombre)
                .orElseThrow();
        var object = s3Service.getObjet(doc);
        ByteArrayResource responseBody = new ByteArrayResource(object);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.inline().filename(doc.getNombre()).build());
        headers.setContentLength(object.length);
        headers.setContentType(MediaType.parseMediaType(doc.getMediaType()));
        return ResponseEntity.ok()
                .headers(headers)
                .body(responseBody);
    }

    @DeleteMapping("/documento/eliminar/{id}")
    Documento eliminarDocumento(@PathVariable Long id) {
        Documento doc = documentoRepository
                .queryByIdAndActivoIsTrue(id).orElseThrow(() -> new DocumentoNotFoundException(id));
        doc.setActivo(false);
        return documentoRepository.save(doc);
    }

    @PostMapping("/documento/crear")
    public Documento handleFileUpload(@RequestParam("file") MultipartFile file,
                                      @RequestParam("nombre") String nombre) throws IOException, URISyntaxException {
        log.info("Creando archivo " + nombre);
        Documento doc = new Documento(nombre);
        doc.setUsuario(usuarioService.getLoggedInUser());
        s3Service.putObjet(file, doc);
        return documentoRepository.save(doc);
    }
}
