/**
 * Nombre del archivo: DocumentoNotFoundException.java
 * Fecha de creacion: 20/09/2022
 * Autor: Carlos Toro
 * Descripcion: Clase que contiene excepcion para manejar errores del modelo
 */

package pe.edu.pucp.dovah.asignaciones.exception;

public class DocumentoNotFoundException extends RuntimeException {
    public DocumentoNotFoundException(Long id) {
        super("No se pudo encontrar el documento con id " + id);
    }
}
