package ch.frostnova.spring.boot.platform.jwt.service.impl;

import ch.frostnova.spring.boot.platform.core.cache.TypeSafeCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

@Component
public class JwtCache extends TypeSafeCache<String, Jws<Claims>> {

    private final static String CACHE_NAME = "jwt-cache";

    public JwtCache() {
        super(CACHE_NAME);
    }
}
