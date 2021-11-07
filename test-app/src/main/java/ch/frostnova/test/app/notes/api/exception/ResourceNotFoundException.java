package ch.frostnova.test.app.notes.api.exception;

import java.util.NoSuchElementException;

/**
 * Exception indicating that a resource was not found.
 */
public class ResourceNotFoundException extends NoSuchElementException {

    public ResourceNotFoundException() {
        this("Not found");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}