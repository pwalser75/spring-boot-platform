package ch.frostnova.spring.boot.platform.jwt.inttest;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@ActiveProfiles({"full", "caching"})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class RestApiTest {

    private final static Logger logger = LoggerFactory.getLogger(RestApiTest.class);

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

    @Test
    void shouldSayHello() {

        String helloAnonymous = client.hello(baseURL(), null);
        assertThat(helloAnonymous).isEqualTo("Hello anonymous");

        String jwt = client.login(baseURL(), "test-tenant", "test-user", null, null);
        String helloUser = client.hello(baseURL(), jwt);
        assertThat(helloUser).isEqualTo("Hello test-user");

        jwt = client.login(baseURL(), "test-tenant", "test-user", emptySet(), Map.of("display-name", "John Doe"));
        String helloNamedUser = client.hello(baseURL(), jwt);
        assertThat(helloNamedUser).isEqualTo("Hello John Doe");
    }

    @Test
    void shouldGetPublicResource() {
        // unauthenticated
        String message = client.publicResource(baseURL(), null);
        assertThat(message).isEqualTo("public");

        // authenticated without roles
        String jwt = client.login(baseURL(), "test-tenant", "test-user", null, null);
        assertThat(client.publicResource(baseURL(), jwt)).isEqualTo("public");
    }

    @Test
    void shouldGetPrivateResource() {
        // unauthenticated
        assertThatThrownBy(() -> client.privateResource(baseURL(), null)).isInstanceOf(HttpClientErrorException.Unauthorized.class);

        // authenticated without roles
        String jwt = client.login(baseURL(), "test-tenant", "test-user", null, null);
        assertThat(client.privateResource(baseURL(), jwt)).isEqualTo("private");
    }

    @Test
    void shouldGetAdminResource() {
        // unauthenticated
        assertThatThrownBy(() -> client.adminResource(baseURL(), null)).isInstanceOf(HttpClientErrorException.Unauthorized.class);

        // unauthorized (no roles)
        String jwtNoRoles = client.login(baseURL(), "test-tenant", "test-user", null, null);
        assertThatThrownBy(() -> client.adminResource(baseURL(), jwtNoRoles)).isInstanceOf(HttpClientErrorException.Forbidden.class);

        // unauthorized (roles, but required role missing)
        String jwtUserRole = client.login(baseURL(), "test-tenant", "test-user", Set.of("tst-user"), null);
        assertThatThrownBy(() -> client.adminResource(baseURL(), jwtUserRole)).isInstanceOf(HttpClientErrorException.Forbidden.class);

        // authenticated, with required role
        String jwtAdminRole = client.login(baseURL(), "test-tenant", "test-user", Set.of("tst-admin", "tst-other"), null);
        assertThat(client.adminResource(baseURL(), jwtAdminRole)).isEqualTo("admin");
    }
}
