package pe.edu.pucp.dovah.RRHH.exceptions;

/*
 * Nombre del archivo: UsuarioNotFoundException
 * Fecha de creación: 20/09/2022 , 18:00
 * Autor: Lloyd Castillo Ramos
 * Descripción: Maneja la excepcion en caso no se encuentre un usuario en la busqueda
 */
public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(int id){

        super("No se pudo encontrar al usuario con id " + id);

    }

}
