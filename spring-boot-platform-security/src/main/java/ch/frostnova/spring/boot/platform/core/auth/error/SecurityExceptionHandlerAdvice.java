package ch.frostnova.spring.boot.platform.core.auth.error;

import ch.frostnova.spring.boot.platform.core.error.ErrorResponse;
import ch.frostnova.spring.boot.platform.security.api.exception.UnauthenticatedException;
import ch.frostnova.spring.boot.platform.security.api.exception.UnauthorizedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Exception handler advice for security exceptions.
 *
 * @author pwalser
 * @since 2021-11-10
 */
@ControllerAdvice
public class SecurityExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UnauthenticatedException.class)
    protected ResponseEntity<Object> handleUnauthenticatedException(UnauthenticatedException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), UNAUTHORIZED, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), FORBIDDEN, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(status.name(), ex, body);
        String logMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        if (status.is5xxServerError()) {
            logger.error(logMessage, ex);
        }
        if (status.is4xxClientError()) {
            if (logger.isDebugEnabled()) {
                logger.debug(logMessage, ex);
            } else {
                logger.info(logMessage);
            }
        }
        return super.handleExceptionInternal(ex, errorResponse, headers, status, request);
    }
}
