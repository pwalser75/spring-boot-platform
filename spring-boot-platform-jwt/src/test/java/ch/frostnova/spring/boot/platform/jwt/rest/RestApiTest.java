package ch.frostnova.spring.boot.platform.jwt.rest;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.core.auth.controller.LoginController;
import ch.frostnova.spring.boot.platform.jwt.TestConfig;
import ch.frostnova.spring.boot.platform.jwt.rest.config.JacksonConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ContextConfiguration(classes = {TestConfig.class, JacksonConfig.class, LoginController.class, TestRestClient.class})
@EnableConfigurationProperties
@EnableAutoConfiguration
public class RestApiTest {

    @Autowired
    private Logger logger;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestClient client;

    private String baseURL() {
        return String.format("http://localhost:%d", port);
    }

    @Test
    void shouldLogIn() {
        String jwt = client.login(baseURL(), "test-tenant", "test-user",
                Set.of("first", "second"),
                Map.of("login-device-id", "device-64738", "login-channel", "mobile", "prospect", "yes"));
        logger.info("JWT: {}", jwt);
        assertThat(jwt).isNotBlank();
    }

    @Test
    void shouldGetAnonymousUserInfo() {

        UserInfo userInfo = client.userInfo(baseURL(), null);
        logger.info("UserInfo: {}", userInfo);
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getTenant()).isNull();
        assertThat(userInfo.getLogin()).isEqualTo("anonymous");
        assertThat(userInfo.getRoles()).isEmpty();
        assertThat(userInfo.getAdditionalClaims()).isEmpty();
    }

    @Test
    void shouldGetAuthenticatedUserInfo() {

        String jwt = client.login(baseURL(), "test-tenant", "test-user",
                Set.of("first", "second"),
                Map.of("login-device-id", "device-64738", "login-channel", "mobile", "prospect", "yes"));

        UserInfo userInfo = client.userInfo(baseURL(), jwt);
        logger.info("UserInfo: {}", userInfo);
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getTenant()).isEqualTo("test-tenant");
        assertThat(userInfo.getLogin()).isEqualTo("test-user");
        assertThat(userInfo.getRoles()).containsExactlyInAnyOrder("first", "second");
        assertThat(userInfo.getAdditionalClaims()).containsAllEntriesOf(Map.of("prospect", "yes", "login-device-id", "device-64738", "login-channel", "mobile"));
    }
}
