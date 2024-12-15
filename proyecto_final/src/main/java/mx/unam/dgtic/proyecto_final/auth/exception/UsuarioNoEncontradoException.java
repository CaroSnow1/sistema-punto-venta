package mx.unam.dgtic.proyecto_final.auth.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UsuarioNoEncontradoException extends Exception {
    public UsuarioNoEncontradoException(Integer id) {
        super("Usuario con ID " + id + " no encontrado.");
    }

    public UsuarioNoEncontradoException(String usuario) {
        super("El usuario ya existe: " + usuario);
    }
}
