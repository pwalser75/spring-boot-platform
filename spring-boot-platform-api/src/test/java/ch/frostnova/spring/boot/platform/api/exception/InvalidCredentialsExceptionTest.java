package ch.frostnova.spring.boot.platform.api.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InvalidCredentialsExceptionTest {

    @Test
    void shouldCreateException() {
        assertThat(new InvalidCredentialsException())
                .extracting(Throwable::getMessage).isEqualTo("Access denied: invalid credentials");

        assertThat(new InvalidCredentialsException("credentials expired"))
                .extracting(Throwable::getMessage).isEqualTo("credentials expired");
    }
}
