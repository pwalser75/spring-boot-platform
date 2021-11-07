package ch.frostnova.spring.boot.platform.core.auth;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;

public interface TokenAuthenticator {

    UserInfo authenticate(String token) throws SecurityException;
}
