package ch.frostnova.spring.boot.platform.core.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface JWTVerificationService {

    Jws<Claims> verify(String token);
}
