package ch.frostnova.spring.boot.platform.core.auth.impl;

import ch.frostnova.spring.boot.platform.core.auth.JWTVerificationService;
import ch.frostnova.spring.boot.platform.core.auth.properties.SigningProperties;
import ch.frostnova.spring.boot.platform.core.cache.TypeSafeCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Optional;

@Service
@ConditionalOnProperty(value = "ch.frostnova.platform.security.signing.public-key")
public class JWTVerificationServiceImpl implements JWTVerificationService {

    private final String CACHE_NAME = "jwt-cache";

    @Autowired
    private Logger logger;

    @Autowired
    private Optional<TypeSafeCache> typeSafeCache;

    @Autowired
    private SigningProperties signingProperties;

    @PostConstruct
    public void init() {
        signingProperties.requirePublicKey();
        logger.info("JWT caching {}", typeSafeCache != null ? "enabled" : "disabled");
    }

    @Override
    public Jws<Claims> verify(String token) {

        // check if we have cached claims for that token
        Optional<Jws<Claims>> cachedToken = typeSafeCache
                .map(cache -> cache.get(CACHE_NAME, token));

        if (cachedToken.isPresent()) {
            // check if token is still valid
            Jws<Claims> jws = cachedToken.get();
            Instant expiresAt = jws.getBody().getExpiration().toInstant();
            if (expiresAt.isBefore(Instant.now())) {
                // remove expired token from cache and throw exception
                typeSafeCache.get().evict(CACHE_NAME, token);
                throw new ExpiredJwtException(jws.getHeader(), jws.getBody(), "Token is expired");
            }
            // return valid cached token
            return jws;
        }
        // parse and validate token
        Jws<Claims> result = Jwts.parser().setSigningKey(signingProperties.requirePublicKey()).parseClaimsJws(token);

        // cache token
        typeSafeCache.ifPresent(cache -> cache.put(CACHE_NAME, token, result));
        return result;
    }
}
