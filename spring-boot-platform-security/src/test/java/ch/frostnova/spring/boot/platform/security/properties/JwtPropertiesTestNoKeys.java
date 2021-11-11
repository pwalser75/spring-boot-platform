package ch.frostnova.spring.boot.platform.security.properties;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ConfigurationProperties
public class JwtPropertiesTestNoKeys {

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    public void shouldReadSigningProperties() {
        assertThat(jwtProperties).isNotNull();
        assertThat(jwtProperties.getPrivateKey()).isNull();
        assertThat(jwtProperties.getPublicKey()).isNull();
    }

    @Test
    public void shouldFailWhenRequirePrivateKey() {
        assertThatThrownBy(() -> jwtProperties.requirePrivateKey()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldFailWhenRequirePublicKey() {
        assertThatThrownBy(() -> jwtProperties.requirePublicKey()).isInstanceOf(UnsupportedOperationException.class);
    }
}
