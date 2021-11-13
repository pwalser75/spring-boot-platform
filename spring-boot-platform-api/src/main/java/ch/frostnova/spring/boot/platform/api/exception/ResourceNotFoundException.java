package ch.frostnova.spring.boot.platform.api.exception;

/**
 * Exception indicating that a resource was not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        this("Not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(Class<?> type, long id) {
        super(String.format("Resource %s:%s not found", type.getSimpleName(), id));
    }

    public ResourceNotFoundException(Class<?> type, String id) {
        super(String.format("Resource %s:%s not found", type.getSimpleName(), id));
    }
}