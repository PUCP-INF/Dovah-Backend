package pe.edu.pucp.dovah.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import pe.edu.pucp.dovah.RRHH.model.Usuario;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor
public class AuthStatusDTO implements Serializable {
    Usuario usuario;
    HttpStatus httpStatus;
    String message;
}
