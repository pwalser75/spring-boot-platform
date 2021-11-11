package ch.frostnova.spring.boot.platform.security.service;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;

import java.time.Duration;
import java.time.OffsetDateTime;

/**
 * Service to issue arbitrary JWT tokens.
 *
 * @author pwalser
 * @since 2021-11-07
 */
public interface JWTSigningService {

    String createJWT(UserInfo userInfo, OffsetDateTime validFrom, Duration validity);
}
