package ch.frostnova.spring.boot.platform.api.exception;

/**
 * Exception thrown when authentication failed due to invalid credentials.
 *
 * @author pwalser
 * @since 2021-11-21
 */
public class InvalidCredentialsException extends SecurityException {

    public InvalidCredentialsException() {
        this("Access denied: invalid credentials");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}
