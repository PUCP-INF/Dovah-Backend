package pe.edu.pucp.dovah.mensajes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.mensajes.model.Hilo;

import java.util.Optional;
import java.util.UUID;

public interface HiloRepository extends JpaRepository<Hilo, Long> {
    @Query("select h from Hilo h left join fetch h.comentarios where h.id = :id")
    Optional<Hilo> queryByIdWithComentarios(Long id);

    @Query("select h from Hilo h left join fetch h.comentarios where h.uuid = :uuid")
    Optional<Hilo> queryByUuidWithComentarios(UUID uuid);
}
