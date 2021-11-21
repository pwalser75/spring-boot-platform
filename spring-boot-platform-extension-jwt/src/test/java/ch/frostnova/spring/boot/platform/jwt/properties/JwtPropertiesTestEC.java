package ch.frostnova.spring.boot.platform.jwt.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.PrivateKey;
import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ConfigurationProperties
@ActiveProfiles("ec-keys")
public class JwtPropertiesTestEC {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void shouldReadSigningProperties() {
        assertThat(jwtProperties).isNotNull();
        assertThat(jwtProperties.getPrivateKey()).isNotBlank();
        assertThat(jwtProperties.getPublicKey()).isNotBlank();
    }

    @Test
    public void shouldRequirePrivateKey() {
        assertThatCode(() -> jwtProperties.requirePrivateKey()).doesNotThrowAnyException();
        PrivateKey privateKey = jwtProperties.requirePrivateKey();
        System.out.println(privateKey.getAlgorithm());
        assertThat(privateKey).isNotNull();
        assertThat(privateKey.getAlgorithm()).isEqualTo("EC");
    }

    @Test
    public void shouldRequirePublicKey() {
        assertThatCode(() -> jwtProperties.requirePublicKey()).doesNotThrowAnyException();
        PublicKey publicKey = jwtProperties.requirePublicKey();
        assertThat(publicKey).isNotNull();
        assertThat(publicKey.getAlgorithm()).isEqualTo("EC");
    }
}
