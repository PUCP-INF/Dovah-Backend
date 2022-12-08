/*
 * Nombre del archivo: FacultadNotFoundException.java
 * Fecha de creacion: 21-09-2022
 * Autor: Victor Avalos
 * Descripción: Definimos el mensaje en caso se lance una excepcion en una busqueda de una Facultad
 */
package pe.edu.pucp.dovah.Gestion.exception;

public class FacultadNotFoundException extends RuntimeException{
    public FacultadNotFoundException(int id){
        super("No se pudo encontrar la facultad con id " + id);
    }
}
