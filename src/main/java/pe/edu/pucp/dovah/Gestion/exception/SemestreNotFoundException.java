package pe.edu.pucp.dovah.Gestion.exception;

public class SemestreNotFoundException extends RuntimeException{

    public SemestreNotFoundException(int id){
        super("No se pudo encontrar el semestre con id " + id);
    }
}
