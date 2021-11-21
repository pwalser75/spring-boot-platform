package ch.frostnova.spring.boot.platform.jwt.service;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.jwt.TestConfig;
import ch.frostnova.spring.boot.platform.jwt.service.impl.JWTTokenAuthenticator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.userInfo;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("ec-keys")
@SpringBootTest(classes = {TestConfig.class})
@EnableConfigurationProperties
public class JWTTokenAuthenticatorTest {

    @Autowired
    private JWTTokenAuthenticator jwtTokenAuthenticator;

    @Autowired
    private JWTSigningService jwtSigningService;

    @Test
    public void shouldAuthenticate() {

        UserInfo request = userInfo("test-login")
                .tenant("test-tenant")
                .roles("RoleA", "RoleB")
                .additionalClaim("loginDeviceId", "device-001")
                .additionalClaim("accessChannel", "web")
                .build();

        String token = jwtSigningService.createJWT(request, OffsetDateTime.now(), Duration.of(2, ChronoUnit.HOURS));

        UserInfo userInfo = jwtTokenAuthenticator.authenticate("Bearer " + token);

        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getTenant()).isEqualTo("test-tenant");
        assertThat(userInfo.getLogin()).isEqualTo("test-login");
        assertThat(userInfo.getRoles()).containsExactlyInAnyOrder("RoleA", "RoleB");
        assertThat(userInfo.getAdditionalClaims().get("loginDeviceId")).isEqualTo("device-001");
        assertThat(userInfo.getAdditionalClaims().get("accessChannel")).isEqualTo("web");
    }

    @Test
    public void shouldSkipUnknownTokenType() {

        String token = "API-KEY " + UUID.randomUUID();

        UserInfo userInfo = jwtTokenAuthenticator.authenticate(token);

        assertThat(userInfo).isNull();
    }

    @Test
    public void shouldSkipUnsupportedBearerTokenType() {

        String token = "Bearer " + UUID.randomUUID();

        UserInfo userInfo = jwtTokenAuthenticator.authenticate(token);

        assertThat(userInfo).isNull();
    }
}
