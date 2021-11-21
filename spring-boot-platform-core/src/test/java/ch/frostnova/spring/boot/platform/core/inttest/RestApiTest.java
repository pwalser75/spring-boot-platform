package ch.frostnova.spring.boot.platform.core.inttest;

import ch.frostnova.spring.boot.platform.api.auth.AuthenticationProvider;
import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

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

    @MockBean
    private AuthenticationProvider authenticationProvider;

    private String baseURL() {
        return String.format("http://localhost:%d", port);
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

        String credentials = registerTestUser(UserInfo.userInfo("test-user")
                .tenant("test-tenant")
                .roles("first", "second")
                .additionalClaim("login-device-id", "device-64738")
                .additionalClaim("login-channel", "mobile")
                .additionalClaim("prospect", "yes")
                .build());

        UserInfo userInfo = client.userInfo(baseURL(), credentials);
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


        String credentials = registerTestUser(UserInfo.userInfo("test-user").build());
        String helloUser = client.hello(baseURL(), credentials);
        assertThat(helloUser).isEqualTo("Hello test-user");

        credentials = registerTestUser(UserInfo.userInfo("test-user").additionalClaim("display-name", "John Doe").build());
        String helloNamedUser = client.hello(baseURL(), credentials);
        assertThat(helloNamedUser).isEqualTo("Hello John Doe");
    }

    @Test
    void shouldGetPublicResource() {
        // unauthenticated
        String message = client.publicResource(baseURL(), null);
        assertThat(message).isEqualTo("public");

        // authenticated without roles
        String credentials = registerTestUser(UserInfo.userInfo("test-user").build());
        assertThat(client.publicResource(baseURL(), credentials)).isEqualTo("public");
    }

    @Test
    void shouldGetPrivateResource() {
        // unauthenticated
        assertThatThrownBy(() -> client.privateResource(baseURL(), null)).isInstanceOf(HttpClientErrorException.Unauthorized.class);

        // authenticated without roles
        String credentials = registerTestUser(UserInfo.userInfo("test-user").build());
        assertThat(client.privateResource(baseURL(), credentials)).isEqualTo("private");
    }

    @Test
    void shouldGetAdminResource() {
        // unauthenticated
        assertThatThrownBy(() -> client.adminResource(baseURL(), null)).isInstanceOf(HttpClientErrorException.Unauthorized.class);

        // unauthorized (no roles)
        String credentialsNoRole = registerTestUser(UserInfo.userInfo("test-user").build());
        assertThatThrownBy(() -> client.adminResource(baseURL(), credentialsNoRole)).isInstanceOf(HttpClientErrorException.Forbidden.class);

        // unauthorized (roles, but required role missing)
        String credentialsUserRole = registerTestUser(UserInfo.userInfo("test-user").roles("tst-user").build());
        assertThatThrownBy(() -> client.adminResource(baseURL(), credentialsUserRole)).isInstanceOf(HttpClientErrorException.Forbidden.class);

        // authenticated, with required role
        String credentialsAdminRole = registerTestUser(UserInfo.userInfo("test-user").roles("tst-admin", "tst-other").build());
        assertThat(client.adminResource(baseURL(), credentialsAdminRole)).isEqualTo("admin");
    }

    private String registerTestUser(UserInfo userInfo) {
        String credentials = UUID.randomUUID().toString();
        when(authenticationProvider.authenticate(eq(credentials))).thenReturn(userInfo);
        return credentials;
    }
}
