package ch.frostnova.spring.boot.platform.security.service;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;

/**
 * Service to authenticate using a (JWT) token.
 *
 * @author pwalser
 * @since 2021-11-07
 */
public interface TokenAuthenticator {

    /**
     * @param token bearer token (currently only JWT supported)
     * @return authenticated user info
     * @throws SecurityException if the token is unsupported, invalid or expired.
     */
    UserInfo authenticate(String token) throws SecurityException;
}
