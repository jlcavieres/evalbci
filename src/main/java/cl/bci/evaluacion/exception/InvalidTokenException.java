package cl.bci.evaluacion.exception;


public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("El token de autenticación no existe o esta mal formado");
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
