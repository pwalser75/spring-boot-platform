package ch.frostnova.spring.boot.platform.core.auth;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;

import java.time.Duration;
import java.time.OffsetDateTime;

public interface JWTSigningService {

    String createJWT(UserInfo userInfo, OffsetDateTime validFrom, Duration validity);
}
