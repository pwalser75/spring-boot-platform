package ch.frostnova.spring.boot.platform.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

/**
 * Base Exception Handler Advice.
 */
public class BaseExceptionHandlerAdvice {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(status.name(), ex, body);
        String logMessage = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        if (status.is5xxServerError()) {
            request.setAttribute("javax.servlet.error.exception", ex, 0);
            logger.error(logMessage, ex);
        }
        if (status.is4xxClientError()) {
            if (logger.isDebugEnabled()) {
                logger.debug(logMessage, ex);
            } else {
                logger.info(logMessage);
            }
        }
        return new ResponseEntity<>(errorResponse, headers, status);
    }
}
