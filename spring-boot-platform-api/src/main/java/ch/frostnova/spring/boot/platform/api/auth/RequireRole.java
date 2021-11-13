package ch.frostnova.spring.boot.platform.api.auth;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotations for methods (preferably in ports such as controllers) to require user principal roles for the caller of those methods.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface RequireRole {

    String value() default "";
}