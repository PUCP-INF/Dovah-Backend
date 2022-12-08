/*
 * Nombre del archivo: CursoNotFoundException.java
 * Fecha de creacion: 21-09-2022
 * Autor: Victor Avalos
 * Descripción: Definimos el mensaje en caso se lance una excepcion en una busqueda de un Curso
 */
package pe.edu.pucp.dovah.Gestion.exception;

public class CursoNotFoundException extends RuntimeException{

    public CursoNotFoundException(int id) {
        super("No se pudo encontrar el curso con id " + id);
    }
}
