package ch.frostnova.spring.boot.platform.core.exception;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.joining;

/**
 * Validation exception.
 */
public class ValidationErrorsException extends RuntimeException {

    private final List<ValidationError> errors = new LinkedList<>();

    public ValidationErrorsException(ConstraintViolationException ex) {
        for (ConstraintViolation<?> error : ex.getConstraintViolations()) {
            errors.add(new ValidationError(error));
        }
    }

    public ValidationErrorsException(BindingResult bindingResult) {
        for (ObjectError error : bindingResult.getAllErrors()) {
            errors.add(new ValidationError(error));
        }
    }

    public ValidationErrorsException(List<ValidationError> validationErrors) {
        errors.addAll(validationErrors);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    @Override
    public String getMessage() {
        return errors.stream().map(ValidationError::toString).collect(joining(", "));
    }
}