package ch.frostnova.spring.boot.platform.jwt.service;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.jwt.TestConfig;
import ch.frostnova.spring.boot.platform.jwt.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.UUID;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.userInfo;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles({"ec-keys", "caching"})
@SpringBootTest(classes = {TestConfig.class})
@EnableConfigurationProperties
public class JWTServiceTest {

    private final static Logger logger = LoggerFactory.getLogger(JWTServiceTest.class);

    @Autowired
    private JWTSigningService jwtSigningService;

    @Autowired
    private JWTVerificationService jwtVerificationService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired(required = false)
    private CacheManager cacheManager;

    @Test
    @SuppressWarnings("unchecked")
    public void testGenerateJWT() {

        Duration duration = Duration.of(42, ChronoUnit.MINUTES);
        OffsetDateTime validFrom = OffsetDateTime.now().truncatedTo(SECONDS);

        UserInfo userInfo = userInfo("test-user")
                .tenant("test-tenant")
                .role("RoleA")
                .role("RoleB")
                .role("RoleC")
                .additionalClaim("loginDeviceId", UUID.randomUUID().toString())
                .additionalClaim("accessChannel", "mobile")
                .build();

        String token = jwtSigningService.createJWT(userInfo, validFrom, duration);
        logger.info("Token: {}", token);

        Jws<Claims> jwt = jwtVerificationService.verify(token);

        Claims body = jwt.getBody();
        String issuer = body.getIssuer();
        Instant issuedAt = body.getIssuedAt().toInstant();
        String tenant = body.get(jwtProperties.getClaimTenant(), String.class);
        String subject = body.getSubject();
        Instant notBefore = body.getNotBefore().toInstant();
        Instant notAfter = body.getExpiration().toInstant();

        Collection<String> scopes = body.get(jwtProperties.getClaimRoles(), Collection.class);

        Assertions.assertThat(issuer).isEqualTo("spring-platform-security-test");
        Assertions.assertThat(tenant).isEqualTo("test-tenant");
        Assertions.assertThat(subject).isEqualTo("test-user");

        Assertions.assertThat(issuedAt.isBefore(validFrom.toInstant())).isFalse();
        Assertions.assertThat(issuedAt.isAfter(validFrom.toInstant())).isFalse();
        Assertions.assertThat(notBefore.isBefore(validFrom.toInstant())).isFalse();
        Assertions.assertThat(notBefore.isAfter(validFrom.toInstant())).isFalse();
        Assertions.assertThat(notAfter.isBefore(validFrom.toInstant().plus(duration))).isFalse();
        Assertions.assertThat(notAfter.isAfter(validFrom.toInstant().plus(duration))).isFalse();
        Assertions.assertThat(scopes).isNotNull();

        assertThat(userInfo.getRoles()).allSatisfy(role -> Assertions.assertThat(scopes.contains(role)).isTrue());
        assertThat(userInfo.getAdditionalClaims()).allSatisfy((k, v) -> assertThat(body.get(k, v.getClass())).isEqualTo(v));
    }

    @Test
    public void testJWTExpiration() throws Exception {

        UserInfo userInfo = userInfo("test-user").tenant("test-tenant").build();
        String token = jwtSigningService.createJWT(userInfo, OffsetDateTime.now(), Duration.of(1, SECONDS));
        jwtVerificationService.verify(token);

        Thread.sleep(1000);
        Assertions.assertThatThrownBy(() -> jwtVerificationService.verify(token)).isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    public void testJWTCache() {

        UserInfo userInfo = userInfo("test-user").tenant("test-tenant").build();
        String token = jwtSigningService.createJWT(userInfo, OffsetDateTime.now(), Duration.of(1, HOURS));
        Jws<Claims> value = jwtVerificationService.verify(token);

        assertThat(jwtVerificationService.verify(token)).isSameAs(value);
    }
}
