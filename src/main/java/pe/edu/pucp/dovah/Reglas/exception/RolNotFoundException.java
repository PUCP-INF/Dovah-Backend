/*
 * Nombre del archivo: RolNotFoundException
 * Fecha de creación: 1/10/2022 , 08:22
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.Reglas.exception;

public class RolNotFoundException extends RuntimeException {
    public RolNotFoundException(int id){

        super("No se pudo encontrar el rol con id " + id);

    }
}
