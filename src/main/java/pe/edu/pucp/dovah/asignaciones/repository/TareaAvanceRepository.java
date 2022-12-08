/*
 * Nombre del archivo: TareaAvanceRepository
 * Fecha de creación: 16/11/2022 , 01:02
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.asignaciones.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.pucp.dovah.asignaciones.model.TareaAvance;

public interface TareaAvanceRepository extends JpaRepository<TareaAvance,Long> {
}
