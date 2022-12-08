package pe.edu.pucp.dovah.mensajes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.dovah.mensajes.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}
