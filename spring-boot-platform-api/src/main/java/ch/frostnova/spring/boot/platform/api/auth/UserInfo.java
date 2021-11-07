package ch.frostnova.spring.boot.platform.api.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.text.Collator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

import static java.util.stream.Collectors.joining;

/**
 * User Information object, contains the <code>login</code> (mandatory) and optional <code>tenant</code>,
 * <code>roles</code> and <code>additionalClaims</code> of an anonymous or authenticated user.
 * <p>
 * The {@link UserInfo} can be obtained over the injectable {@link UserInfoProvider}.
 * <p>
 * For tests, UserInfo instances can be created using the <code>userInfo()</code> <b>builder</b> method:
 * <code>
 * <pre>
 * UserInfo userInfo = userInfo("USER-01").build();
 *
 * UserInfo userInfo = userInfo("USER-01").tenant("test-tenant").build();
 *
 * UserInfo userInfo = userInfo("USER-01")
 *     .tenant("test-tenant")
 *     .role("author")
 *     .role("publisher")
 *     .additionalClaim(login-device.id", "device-64738")
 *     .additionalClaim("login-channel", "mobile")
 *     .additionalClaim("prospect", "yes")
 *     .build();
 *
 * UserInfo userInfo = userInfo("USER-01")
 *     .tenant("test-tenant")
 *     .roles(Set.of("author", "publisher")
 *     .additionalClaims(login-device.id", "device-64738", "login-channel", "mobile")
 *     .build();</pre>
 * </code>
 *
 * @author pwalser
 * @since 2021-11-07
 */
@ApiModel("UserInfo")
public class UserInfo {

    private final static UserInfo ANONYMOUS = userInfo("anonymous").build();

    private final String login;
    private final Set<String> roles = new TreeSet<>(Collator.getInstance());
    private final Map<String, String> additionalClaims = new TreeMap<>(Collator.getInstance());
    private String tenant;

    private UserInfo(String login) {
        if (login == null || login.trim().length() == 0) {
            throw new IllegalArgumentException("Login is required");
        }
        if (login.length() != login.trim().length()) {
            throw new IllegalArgumentException("Login must not have leading/trailing whitespaces");
        }
        this.login = login;
    }

    public static UserInfo anonymous() {
        return ANONYMOUS;
    }

    public static Builder userInfo(String login) {
        return new Builder(login);
    }

    @ApiModelProperty(notes = "tenant id for the user (multitenancy support)", example = "tenant123")
    public String getTenant() {
        return tenant;
    }

    @ApiModelProperty(notes = "login id of the user", example = "USER123")
    public String getLogin() {
        return login;
    }

    @ApiModelProperty(notes = "set of granted roles", example = "foo, bla")
    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    @ApiModelProperty(notes = "map of additional claims")
    public Map<String, String> getAdditionalClaims() {
        return additionalClaims;
    }

    public boolean isAnonymous() {
        return this == ANONYMOUS;
    }

    public boolean isAuthenticated() {
        return !isAnonymous();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(tenant, userInfo.tenant) &&
                Objects.equals(login, userInfo.login);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> values = new LinkedList<>();
        stringBuilder.append("UserInfo{");
        values.add("login=" + login);
        if (tenant != null) {
            values.add("tenant=" + tenant);
        }
        if (!roles.isEmpty()) {
            values.add("roles=" + roles.stream().collect(joining("/")));
        }
        if (isAnonymous()) {
            values.add("authenticated=" + isAuthenticated());
        }
        stringBuilder.append(values.stream().collect(joining(", ")));
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenant, login);
    }

    public static class Builder {

        private final UserInfo instance;
        private boolean consumed;

        public Builder(String login) {
            instance = new UserInfo(login);
        }

        private Builder set(Consumer<UserInfo> access) {
            if (consumed) {
                throw new IllegalStateException("already consumed");
            }
            access.accept(instance);
            return this;
        }

        public Builder tenant(String tenant) {
            return set(x -> x.tenant = tenant);
        }

        public Builder roles(Set<String> roles) {
            return set(x -> {
                x.roles.clear();
                if (roles != null) {
                    x.roles.addAll(roles);
                }
            });
        }

        public Builder role(String role) {
            return set(x -> x.roles.add(role));
        }

        public Builder additionalClaims(Map<String, String> additionalClaims) {
            return set(x -> {
                x.additionalClaims.clear();
                if (additionalClaims != null) {
                    x.additionalClaims.putAll(additionalClaims);
                }
            });
        }

        public Builder additionalClaim(String key, String value) {
            return set(x -> x.additionalClaims.put(key, value));
        }

        public UserInfo build() {
            consumed = true;
            return instance;
        }
    }
}
