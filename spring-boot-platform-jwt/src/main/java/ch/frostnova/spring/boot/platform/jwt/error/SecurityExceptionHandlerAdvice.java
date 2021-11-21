package ch.frostnova.spring.boot.platform.jwt.error;

import ch.frostnova.spring.boot.platform.api.exception.InvalidCredentialsException;
import ch.frostnova.spring.boot.platform.api.exception.UnauthenticatedException;
import ch.frostnova.spring.boot.platform.api.exception.UnauthorizedException;
import ch.frostnova.spring.boot.platform.core.exception.BaseExceptionHandlerAdvice;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Exception handler advice for security exceptions.
 *
 * @author pwalser
 * @since 2021-11-10
 */
@ControllerAdvice
public class SecurityExceptionHandlerAdvice extends BaseExceptionHandlerAdvice {

    @ExceptionHandler(UnauthenticatedException.class)
    protected ResponseEntity<Object> handleUnauthenticatedException(UnauthenticatedException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), UNAUTHORIZED, request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    protected ResponseEntity<Object> handleInvalidCredentialsException(UnauthorizedException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), UNAUTHORIZED, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), FORBIDDEN, request);
    }
}
