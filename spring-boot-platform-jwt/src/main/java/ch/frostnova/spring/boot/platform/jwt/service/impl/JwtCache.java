package ch.frostnova.spring.boot.platform.jwt.service.impl;

import ch.frostnova.spring.boot.platform.core.cache.TypeSafeCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.util.Base64;

import static ch.frostnova.spring.boot.platform.core.util.StringUtils.sha256;

@Component
public class JwtCache extends TypeSafeCache<String, Jws<Claims>> {

    private final static String CACHE_NAME = "jwt-cache";

    public JwtCache() {
        super(CACHE_NAME);
    }

    @Override
    protected Object cacheKey(String key) {
        return hashedKey(key);
    }

    private String hashedKey(String key) {
        return Base64.getEncoder().encodeToString(sha256(key));
    }
}
