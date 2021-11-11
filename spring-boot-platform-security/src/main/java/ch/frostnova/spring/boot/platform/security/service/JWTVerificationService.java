package ch.frostnova.spring.boot.platform.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

/**
 * Service to parse and verify JWT tokens.
 *
 * @author pwalser
 * @since 2021-11-07
 */
public interface JWTVerificationService {

    /**
     * Parse and verify (valid signature, not expired) a JWT token
     *
     * @param token JWT token
     * @return claims of verified token
     */
    Jws<Claims> verify(String token);
}
