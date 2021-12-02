package ch.frostnova.spring.boot.platform.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.anonymous;
import static ch.frostnova.spring.boot.platform.api.auth.UserInfo.userInfo;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT;
import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for the {@link UserInfo}.
 *
 * @author pwalser
 * @since 2021-11-07
 */
public class UserInfoTest {

    public static ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
                .registerModule(new JavaTimeModule())
                .setDateFormat(new StdDateFormat())
                .enable(INDENT_OUTPUT)
                .enable(ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .enable(ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .disable(WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(NON_EMPTY);
    }

    @Test
    public void shouldGetAnonymous() {
        UserInfo anonymous = anonymous();
        assertThat(anonymous).isNotNull();
        assertThat(anonymous.isAnonymous()).isTrue();
        assertThat(anonymous.isAuthenticated()).isFalse();
        assertThat(anonymous.getLogin()).isEqualTo("anonymous");
        assertThat(anonymous.getTenant()).isNull();
        assertThat(anonymous.getRoles()).isEmpty();
        assertThat(anonymous.getAdditionalClaims()).isEmpty();
    }

    @Test
    public void shouldNotCreateTrivialUserInfo() {
        assertThatThrownBy(() -> userInfo(null).build()).isInstanceOfSatisfying(IllegalArgumentException.class,
                ex -> assertThat(ex.getMessage()).isEqualTo("Login is required"));
        assertThatThrownBy(() -> userInfo("   ").build()).isInstanceOfSatisfying(IllegalArgumentException.class,
                ex -> assertThat(ex.getMessage()).isEqualTo("Login is required"));
        assertThatThrownBy(() -> userInfo("\t").build()).isInstanceOfSatisfying(IllegalArgumentException.class,
                ex -> assertThat(ex.getMessage()).isEqualTo("Login is required"));

        assertThatThrownBy(() -> userInfo(" USER01").build()).isInstanceOfSatisfying(IllegalArgumentException.class,
                ex -> assertThat(ex.getMessage()).isEqualTo("Login must not have leading/trailing whitespaces"));
        assertThatThrownBy(() -> userInfo("USER01 ").build()).isInstanceOfSatisfying(IllegalArgumentException.class,
                ex -> assertThat(ex.getMessage()).isEqualTo("Login must not have leading/trailing whitespaces"));
        assertThatThrownBy(() -> userInfo("\tUSER01").build()).isInstanceOfSatisfying(IllegalArgumentException.class,
                ex -> assertThat(ex.getMessage()).isEqualTo("Login must not have leading/trailing whitespaces"));
        assertThatThrownBy(() -> userInfo("USER01\n").build()).isInstanceOfSatisfying(IllegalArgumentException.class,
                ex -> assertThat(ex.getMessage()).isEqualTo("Login must not have leading/trailing whitespaces"));
    }

    @Test
    public void shouldCreateSimpleUserInfo() {
        UserInfo userInfo = userInfo("USER-01").build();
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.isAnonymous()).isFalse();
        assertThat(userInfo.isAuthenticated()).isTrue();
        assertThat(userInfo.getTenant()).isNull();
        assertThat(userInfo.getLogin()).isEqualTo("USER-01");
        assertThat(userInfo.getRoles()).isEmpty();
        assertThat(userInfo.getAdditionalClaims()).isEmpty();
    }

    @Test
    public void shouldCreateExtendendUserInfo() {
        UserInfo userInfo = userInfo("USER 01")
                .tenant("test-tenant")
                .roles(Set.of("PUBLISHER", "Reviewer"))
                .roles("author")
                .roles(emptySet())
                .roles((Set<String>) null)
                .additionalClaims(Map.of("login-device-id", "device-64738", "login-channel", "mobile"))
                .additionalClaim("prospect", "yes")
                .additionalClaims(emptyMap())
                .additionalClaims(null)
                .build();
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.isAnonymous()).isFalse();
        assertThat(userInfo.isAuthenticated()).isTrue();
        assertThat(userInfo.getLogin()).isEqualTo("USER 01");
        assertThat(userInfo.getTenant()).isEqualTo("test-tenant");
        assertThat(userInfo.getRoles()).containsExactlyInAnyOrder("author", "PUBLISHER", "Reviewer");
        assertThat(userInfo.getAdditionalClaims()).isEqualTo(Map.of("prospect", "yes", "login-device-id", "device-64738", "login-channel", "mobile"));
    }

    @Test
    public void shouldAllowNoModificationsToBuilderWhenConsumed() {
        UserInfo.Builder builder = userInfo("USER 01");
        builder.build();
        assertThatThrownBy(() -> builder.roles("additional")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldReturnSameInstanceWhenBuiltAgain() {
        UserInfo.Builder builder = userInfo("USER 01");
        UserInfo a = builder.build();
        UserInfo b = builder.build();
        assertThat(a).isSameAs(b);
    }

    @Test
    public void shouldHaveCorrectEqualsHashCodeToString() {
        UserInfo a = userInfo("USER-01")
                .tenant("test-tenant")
                .build();

        UserInfo b = userInfo("USER-01")
                .tenant("test-tenant")
                .roles("author")
                .roles("publisher")
                .additionalClaim("login-device-id", "device-12345")
                .additionalClaim("login-channel", "mobile")
                .build();

        UserInfo c = userInfo("USER-01")
                .tenant("test-tenant")
                .roles("channel-admin")
                .additionalClaim("login-device-id", "device-56789")
                .additionalClaim("login-channel", "web")
                .build();

        assertThat(a).isEqualTo(a);
        assertThat(a).isEqualTo(b);
        assertThat(b).isEqualTo(a);
        assertThat(a).isEqualTo(c);
        assertThat(c).isEqualTo(a);
        assertThat(b).isEqualTo(c);
        assertThat(c).isEqualTo(b);

        assertThat(a.equals(null)).isFalse();

        assertThat(a).isNotEqualTo(userInfo("USER-01").build());
        assertThat(a).isNotEqualTo(userInfo("USER-01").tenant("other-tenant").build());
        assertThat(a).isNotEqualTo(userInfo("USER-02").tenant("test-tenant").build());

        assertThat(a.hashCode()).isEqualTo(b.hashCode());
        assertThat(b.hashCode()).isEqualTo(c.hashCode());

        assertThat(userInfo("USER-01").build().toString()).isEqualTo("UserInfo{login=USER-01}");
        assertThat(a.toString()).isEqualTo("UserInfo{login=USER-01, tenant=test-tenant}");
        assertThat(b.toString()).isEqualTo("UserInfo{login=USER-01, tenant=test-tenant, roles=author/publisher}");
        assertThat(c.toString()).isEqualTo("UserInfo{login=USER-01, tenant=test-tenant, roles=channel-admin}");
        assertThat(anonymous().toString()).isEqualTo("UserInfo{login=anonymous, authenticated=false}");
    }

    @Test
    public void shouldSerializeAndDeserialize() throws IOException {

        UserInfo userInfo = userInfo("USER-01")
                .tenant("test-tenant")
                .roles("channel-admin")
                .additionalClaim("login-device-id", "device-56789")
                .additionalClaim("login-channel", "web")
                .build();

        String json = objectMapper().writeValueAsString(userInfo);
        System.out.println(json);

        UserInfo restored = objectMapper().readValue(json, UserInfo.class);
        assertThat(restored).isEqualTo(userInfo);
        assertThat(restored.getLogin()).isEqualTo(userInfo.getLogin());
        assertThat(restored.getTenant()).isEqualTo(userInfo.getTenant());
        assertThat(restored.getRoles()).isEqualTo(userInfo.getRoles());
        assertThat(restored.getAdditionalClaims()).isEqualTo(userInfo.getAdditionalClaims());
        assertThat(restored.isAuthenticated()).isEqualTo(userInfo.isAuthenticated());
        assertThat(restored.isAnonymous()).isEqualTo(userInfo.isAnonymous());
    }
}
