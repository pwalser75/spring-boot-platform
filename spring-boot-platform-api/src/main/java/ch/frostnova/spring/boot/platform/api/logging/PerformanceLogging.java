package ch.frostnova.spring.boot.platform.api.logging;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotations for classes/methods whose performance should be logged.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface PerformanceLogging {
}