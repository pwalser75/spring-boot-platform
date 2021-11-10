package ch.frostnova.spring.boot.platform.jwt.properties;

import ch.frostnova.spring.boot.platform.core.auth.properties.SigningProperties;
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
public class SigningPropertiesTestPublicKeyOnly {

    @Autowired
    private SigningProperties signingProperties;

    @Test
    public void shouldReadSigningProperties() {
        assertThat(signingProperties).isNotNull();
        assertThat(signingProperties.getPrivateKey()).isNull();
        assertThat(signingProperties.getPublicKey()).isNotBlank();
    }

    @Test
    public void shouldFailWhenRequirePrivateKey() {
        assertThatThrownBy(() -> signingProperties.requirePrivateKey()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldRequirePublicKey() {
        assertThatCode(() -> signingProperties.requirePublicKey()).doesNotThrowAnyException();
        PublicKey publicKey = signingProperties.requirePublicKey();
        assertThat(publicKey).isNotNull();
        assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");

    }
}
