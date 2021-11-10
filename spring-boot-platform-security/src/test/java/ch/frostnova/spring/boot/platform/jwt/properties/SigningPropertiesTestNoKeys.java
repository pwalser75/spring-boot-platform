package ch.frostnova.spring.boot.platform.jwt.properties;

import ch.frostnova.spring.boot.platform.core.auth.properties.SigningProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ConfigurationProperties
public class SigningPropertiesTestNoKeys {

    @Autowired
    private SigningProperties signingProperties;

    @Test
    public void shouldReadSigningProperties() {
        assertThat(signingProperties).isNotNull();
        assertThat(signingProperties.getPrivateKey()).isNull();
        assertThat(signingProperties.getPublicKey()).isNull();
    }

    @Test
    public void shouldFailWhenRequirePrivateKey() {
        assertThatThrownBy(() -> signingProperties.requirePrivateKey()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldFailWhenRequirePublicKey() {
        assertThatThrownBy(() -> signingProperties.requirePublicKey()).isInstanceOf(UnsupportedOperationException.class);
    }
}
