package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.edu.pucp.dovah.asignaciones.model.Documento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    Optional<Documento> queryByIdAndActivoIsTrue(Long id);

    List<Documento> queryAllByActivoIsTrue();

    Optional<Documento> queryByUuidAndNombreAndActivoIsTrue(UUID uuid, String nombre);
}
