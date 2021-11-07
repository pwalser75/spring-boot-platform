package ch.frostnova.spring.boot.platform.core.auth;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;

import java.time.Duration;
import java.time.OffsetDateTime;

public interface JWTSigningService {

    String createJWT(UserInfo userInfo, OffsetDateTime validFrom, Duration validity);
}
