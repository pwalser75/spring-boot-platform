package ch.frostnova.spring.boot.platform.security.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.security.PublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ConfigurationProperties
@ActiveProfiles("publickeyonly")
public class JwtPropertiesTestPublicKeyOnly {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void shouldReadSigningProperties() {
        assertThat(jwtProperties).isNotNull();
        assertThat(jwtProperties.getPrivateKey()).isNull();
        assertThat(jwtProperties.getPublicKey()).isNotBlank();
    }

    @Test
    public void shouldFailWhenRequirePrivateKey() {
        assertThatThrownBy(() -> jwtProperties.requirePrivateKey()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldRequirePublicKey() {
        assertThatCode(() -> jwtProperties.requirePublicKey()).doesNotThrowAnyException();
        PublicKey publicKey = jwtProperties.requirePublicKey();
        assertThat(publicKey).isNotNull();
        assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");

    }
}
