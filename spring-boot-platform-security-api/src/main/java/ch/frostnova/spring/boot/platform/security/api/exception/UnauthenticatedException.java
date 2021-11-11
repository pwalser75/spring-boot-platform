package ch.frostnova.spring.boot.platform.security.api.exception;

/**
 * Exception thrown when an unauthenticated user attempts an operation which requires authentication.
 *
 * @author pwalser
 * @since 2021-11-10
 */
public class UnauthenticatedException extends SecurityException {

    public UnauthenticatedException() {
        this("Access denied: unauthorized");
    }

    public UnauthenticatedException(String message) {
        super(message);
    }
}
