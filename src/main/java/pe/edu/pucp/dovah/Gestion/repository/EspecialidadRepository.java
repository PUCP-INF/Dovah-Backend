/*
 * Nombre del archivo: EspecialidadRepository.java
 * Fecha de creacion: 20-09-2022
 * Autor: Victor Avalos
 * Descripción: Interfaz de repositorio para la clase Especialidad
 */
package pe.edu.pucp.dovah.Gestion.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.dovah.Gestion.model.Especialidad;

import java.util.List;
import java.util.Optional;

public interface EspecialidadRepository extends JpaRepository<Especialidad,Integer> {
    List<Especialidad> queryAllByActivoIsTrue();
    @EntityGraph(attributePaths = {"cursos"})
    Optional<Especialidad> findByIdEspecialidadAndActivoIsTrue(int idEspecialidad);
    List<Especialidad> findByFacultad_IdFacultadAndActivoIsTrue(int idFacultad);

    Optional<Especialidad> queryByCodigoAndActivoIsTrue(String codigo);
}
