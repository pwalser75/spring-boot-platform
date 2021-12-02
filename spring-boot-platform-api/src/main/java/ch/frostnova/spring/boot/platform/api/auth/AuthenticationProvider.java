package ch.frostnova.spring.boot.platform.api.auth;

import ch.frostnova.spring.boot.platform.api.exception.InvalidCredentialsException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An authentication provider can authenticate a user based on an HTTP 'Authentication' header.
 * This is a strategy interface, and multiple implementations may co-exist. Each discovered implementation
 * will be asked to authenticate a user, and the first successfully doing so will return the authenticated user.
 * If none exist or none can authenticate the user, the user will be treated as anonymous user.
 *
 * @author pwalser
 * @since 2021-11-21
 */
public interface AuthenticationProvider {

    Pattern BEARER_TOKEN_PATTERN = Pattern.compile("Bearer (.+)");

    /**
     * Attempt to authenticate a user. If successful, the {@link UserInfo} will be returned.
     * If not valid (e.g. wrong password, invalid or expired JWT), a {@link InvalidCredentialsException} or
     * other SecurityException shall be thrown.
     * If not applicable (e.g. expecting 'bearer' authentication, but it's something different), null should be returned,
     * allowing to skip this provider and attempting to authenticate using another {@link AuthenticationProvider}.
     *
     * @param authentication value of the HTTP 'Authentication' request header, never null
     * @return user info if authenticated, null if not applicable, or SecurityException.
     */
    UserInfo authenticate(String authentication) throws SecurityException;

    default String getBearerToken(String authentication) {
        Matcher matcher = BEARER_TOKEN_PATTERN.matcher(authentication);
        return matcher.matches() ? matcher.group(1) : null;
    }
}
