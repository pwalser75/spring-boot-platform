package ch.frostnova.spring.boot.platform.core.exception;

import ch.frostnova.spring.boot.platform.api.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Exception handler advice.
 */
@ControllerAdvice
public class GeneralExceptionHandlerAdvice extends BaseExceptionHandlerAdvice {

    @ExceptionHandler({NoSuchElementException.class, ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleNotFound(RuntimeException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), NOT_FOUND, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        ValidationErrorsException validationErrorsException = new ValidationErrorsException(ex);
        return handleExceptionInternal(ex, validationErrorsException.getErrors(), new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(ValidationErrorsException.class)
    protected ResponseEntity<Object> ValidationErrorsException(ValidationErrorsException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getErrors(), new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        ValidationErrorsException validationErrorsException = new ValidationErrorsException(ex);
        return handleExceptionInternal(ex, validationErrorsException.getErrors(), new HttpHeaders(), BAD_REQUEST, request);
    }
}
