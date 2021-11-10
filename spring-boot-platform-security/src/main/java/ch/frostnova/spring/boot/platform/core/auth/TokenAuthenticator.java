package ch.frostnova.spring.boot.platform.core.auth;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;

public interface TokenAuthenticator {

    UserInfo authenticate(String token) throws SecurityException;
}
