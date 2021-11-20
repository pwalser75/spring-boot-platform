package ch.frostnova.spring.boot.platform.api.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceNotFoundExceptionTest {

    @Test
    void shouldCreateException() {
        assertThat(new ResourceNotFoundException())
                .extracting(Throwable::getMessage).isEqualTo("Not found");

        assertThat(new ResourceNotFoundException("Ain't there"))
                .extracting(Throwable::getMessage).isEqualTo("Ain't there");

        assertThat(new ResourceNotFoundException(TestResource.class, 123))
                .extracting(Throwable::getMessage).isEqualTo("Resource TestResource:123 not found");

        String stringId = UUID.randomUUID().toString();
        assertThat(new ResourceNotFoundException(TestResource.class, stringId))
                .extracting(Throwable::getMessage).isEqualTo("Resource TestResource:" + stringId + " not found");
    }

    private static class TestResource {

    }
}
