/*
 * Nombre del archivo: PlanTesisNotFoundException
 * Fecha de creación: 18/10/2022 , 11:49
 * Autor: Lloyd Erwin Castillo Ramos
 * Descripción:
 */
package pe.edu.pucp.dovah.PlanTesis.exception;

public class PlanTesisNotFoundException extends RuntimeException{
    public PlanTesisNotFoundException(int id){

        super("No se puedo encontar el plan de tesis con id " + id);

    }
}
