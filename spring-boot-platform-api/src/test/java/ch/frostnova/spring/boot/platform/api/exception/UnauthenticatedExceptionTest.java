package ch.frostnova.spring.boot.platform.api.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnauthenticatedExceptionTest {

    @Test
    void shouldCreateException() {
        assertThat(new UnauthenticatedException())
                .extracting(Throwable::getMessage).isEqualTo("Access denied: unauthorized");

        assertThat(new UnauthenticatedException("Speak friend and enter"))
                .extracting(Throwable::getMessage).isEqualTo("Speak friend and enter");
    }
}
