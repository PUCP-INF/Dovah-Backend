/**
 * Nombre del archivo: TareaNotFoundException.java
 * Fecha de creacion: 20/09/2022
 * Autor: Carlos Toro
 * Descripcion: Clase que contiene excepcion para manejar errores del modelo
 */

package pe.edu.pucp.dovah.asignaciones.exception;

public class TareaNotFoundException extends RuntimeException {
    public TareaNotFoundException(Long id) {
        super("No se pudo encontrar la tarea con id " + id);
    }
}
