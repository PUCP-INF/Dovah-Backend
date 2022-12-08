/*
 * Nombre del archivo: EspecialidadNotFoundException.java
 * Fecha de creacion: 21-09-2022
 * Autor: Victor Avalos
 * Descripción: Definimos el mensaje en caso se lance una excepcion en una busqueda de una Especialidad
 */
package pe.edu.pucp.dovah.Gestion.exception;

public class EspecialidadNotFoundException extends RuntimeException{

    public EspecialidadNotFoundException(int id){
        super("No se pudo encontrar la especialidad con id " + id);
    }
}
