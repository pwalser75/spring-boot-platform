package ch.frostnova.spring.boot.platform.core.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

/**
 * Validation error.
 */
public class ValidationError {

    @JsonProperty("path")
    private final String path;

    @JsonProperty("code")
    private final String errorCode;

    @JsonProperty("message")
    private final String message;

    @JsonCreator
    public ValidationError(@JsonProperty("path") String path,
                           @JsonProperty("code") String errorCode,
                           @JsonProperty("message") String message) {
        this.path = path;
        this.errorCode = errorCode;
        this.message = message;
    }

    public ValidationError(ObjectError objectError) {

        String model = objectError.getObjectName();
        String field = objectError instanceof FieldError ? ((FieldError) objectError).getField() : null;

        path = Stream.of(model, field).filter(Objects::nonNull).collect(joining(":"));
        this.errorCode = objectError.getCode();
        this.message = objectError.getDefaultMessage();
    }

    public ValidationError(ConstraintViolation<?> constraintViolation) {

        this.path = constraintViolation.getPropertyPath().toString();
        this.errorCode = "ConstraintViolation";
        this.message = constraintViolation.getMessage();

    }

    public String getPath() {
        return path;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}