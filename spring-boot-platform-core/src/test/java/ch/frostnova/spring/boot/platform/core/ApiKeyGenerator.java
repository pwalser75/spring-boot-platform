package ch.frostnova.spring.boot.platform.core;

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiKeyGenerator {

    @Test
    public void shouldGenerateApiKey() throws NoSuchAlgorithmException {
        int bits = 1024; // needs to be a multiple of 8

        byte[] apiKey = new byte[bits / 8];
        new SecureRandom().nextBytes(apiKey);
        String apiKeyBase64 = Base64.getEncoder().encodeToString(apiKey);
        System.out.println("secret base64: " + apiKeyBase64);
        byte[] hashedapiKey = MessageDigest.getInstance("SHA-256").digest(apiKey);
        String hashedapiKeyBase64 = Base64.getEncoder().encodeToString(hashedapiKey);
        System.out.println("hashedSecret base64: " + hashedapiKeyBase64);
    }

    @Test
    void shouldVerifyApiKey() throws NoSuchAlgorithmException {

        String apiKeyBase64 = "56A+ee+UVV/uKLqd0C1rUHBcROSWyxB3RP7+q6O8v9VgBvaIpkxmUerCHa+mM3XwPfxn8Y25kMUFt7RF" +
                "DirNa7nxOfEJ+t7wbdGBB9LmytA3gY4Affei2e3qPYoWODUsYDm7I8qaK/dbdQoBFx2HLok3OHU8Ta+fei2bv2juRlo=";

        byte[] apiKey = Base64.getDecoder().decode(apiKeyBase64);
        byte[] hashedApiKey = MessageDigest.getInstance("SHA-256").digest(apiKey);
        String hashedapiKeyBase64 = Base64.getEncoder().encodeToString(hashedApiKey);
        assertThat(hashedapiKeyBase64).isEqualTo("ZPQFcRExwKGpVxNUHRNLGBUYhMy4IOT3rp2D781dGtI=");
    }
}

