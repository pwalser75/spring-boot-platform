package ch.frostnova.spring.boot.platform.api.exception;

/**
 * Exception thrown when an authenticated user is not allowed to perform a certain operation.
 *
 * @author pwalser
 * @since 2021-11-10
 */
public class UnauthorizedException extends SecurityException {

    public UnauthorizedException() {
        this("Access denied: authentication required");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
