package ch.frostnova.spring.boot.platform.jwt.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class SigningServiceTest {

    @Autowired
    private SigningService signingService;

    @Test
    public void shouldSignAndVerifySuccessfully() throws Exception {
        byte[] data = new byte[12345];
        ThreadLocalRandom.current().nextBytes(data);

        byte[] signature = signingService.sign(data);
        assertThat(signature).isNotNull();
        assertThat(signature.length).isGreaterThan(0);
        assertThat(signingService.verify(data, signature)).isTrue();
    }

    @Test
    public void shouldFailOnTamperedData() throws Exception {
        byte[] data = new byte[12345];
        ThreadLocalRandom.current().nextBytes(data);

        byte[] signature = signingService.sign(data);

        byte[] tamperedData = new byte[data.length];
        System.arraycopy(data, 0, tamperedData, 0, signature.length);
        int randomIndex = ThreadLocalRandom.current().nextInt(0, data.length);
        tamperedData[randomIndex] = (byte) ~tamperedData[randomIndex];
        assertThat(signingService.verify(tamperedData, signature)).isFalse();
    }

    @Test
    public void shouldFailOnInvalidSignature() throws Exception {
        byte[] data = new byte[12345];
        ThreadLocalRandom.current().nextBytes(data);

        byte[] signature = signingService.sign(data);

        byte[] fakeSignature = new byte[signature.length];
        System.arraycopy(signature, 0, fakeSignature, 0, signature.length);
        int randomIndex = ThreadLocalRandom.current().nextInt(0, signature.length);
        fakeSignature[randomIndex] = (byte) ~signature[randomIndex];
        assertThat(signingService.verify(data, fakeSignature)).isFalse();
    }
}
