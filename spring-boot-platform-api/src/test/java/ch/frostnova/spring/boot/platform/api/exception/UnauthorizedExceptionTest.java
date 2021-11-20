package ch.frostnova.spring.boot.platform.api.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnauthorizedExceptionTest {

    @Test
    void shouldCreateException() {
        assertThat(new UnauthorizedException())
                .extracting(Throwable::getMessage).isEqualTo("Access denied: authentication required");

        assertThat(new UnauthorizedException("You no take candle"))
                .extracting(Throwable::getMessage).isEqualTo("You no take candle");
    }
}
