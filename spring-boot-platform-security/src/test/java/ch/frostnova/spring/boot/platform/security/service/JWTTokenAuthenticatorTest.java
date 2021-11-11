package ch.frostnova.spring.boot.platform.security.service;

import ch.frostnova.spring.boot.platform.security.TestConfig;
import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.service.impl.JWTTokenAuthenticator;
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

import static ch.frostnova.spring.boot.platform.security.api.UserInfo.userInfo;
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
                .role("RoleA")
                .role("RoleB")
                .additionalClaim("loginDeviceId", "device-001")
                .additionalClaim("accessChannel", "web")
                .build();

        String token = jwtSigningService.createJWT(request, OffsetDateTime.now(), Duration.of(2, ChronoUnit.HOURS));

        UserInfo userInfo = jwtTokenAuthenticator.authenticate(token);

        assertThat(userInfo.getTenant()).isEqualTo("test-tenant");
        assertThat(userInfo.getLogin()).isEqualTo("test-login");
        assertThat(userInfo.getRoles()).containsExactlyInAnyOrder("RoleA", "RoleB");
        assertThat(userInfo.getAdditionalClaims().get("loginDeviceId")).isEqualTo("device-001");
        assertThat(userInfo.getAdditionalClaims().get("accessChannel")).isEqualTo("web");
    }
}
