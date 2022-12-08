/*
 * Nombre del archivo: FacultadRepository.java
 * Fecha de creacion: 20-09-2022
 * Autor: Victor Avalos
 * Descripción: Interfaz de repositorio para la clase Facultad
 */
package pe.edu.pucp.dovah.Gestion.repository;

import org.hibernate.annotations.SQLUpdate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.pucp.dovah.Gestion.model.Facultad;
import pe.edu.pucp.dovah.asignaciones.model.Tarea;

import java.util.List;
import java.util.Optional;

public interface FacultadRepository extends JpaRepository<Facultad,Integer>{

    List<Facultad> queryAllByActivoIsTrue();
    @EntityGraph(attributePaths = {"especialidades"})
    Optional<Facultad> findByIdFacultadAndActivoIsTrue(int idFacultad);
}
