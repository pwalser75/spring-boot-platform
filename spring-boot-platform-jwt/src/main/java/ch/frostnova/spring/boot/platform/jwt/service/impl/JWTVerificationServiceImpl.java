package ch.frostnova.spring.boot.platform.jwt.service.impl;

import ch.frostnova.spring.boot.platform.jwt.properties.JwtProperties;
import ch.frostnova.spring.boot.platform.jwt.service.JWTVerificationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Service
@ConditionalOnProperty(value = "ch.frostnova.platform.security.jwt.public-key")
public class JWTVerificationServiceImpl implements JWTVerificationService {

    private final String CACHE_NAME = "jwt-cache";

    private final static Logger logger = LoggerFactory.getLogger(JWTVerificationServiceImpl.class);

    @Autowired
    private JwtCache jwtCache;

    @Autowired
    private JwtProperties jwtProperties;

    @PostConstruct
    public void init() {
        jwtProperties.requirePublicKey();
        logger.info("JWT caching {}", jwtCache.isEnabled() ? "enabled" : "disabled");
    }

    @Override
    public Jws<Claims> verify(String token) {

        // check if we have cached claims for that token
        Jws<Claims> cachedClaims = jwtCache.get(token);

        if (cachedClaims != null) {
            // check if token is still valid
            Instant expiresAt = cachedClaims.getBody().getExpiration().toInstant();
            if (expiresAt.isBefore(Instant.now())) {
                // remove expired token from cache and throw exception
                jwtCache.evict(token);
                throw new ExpiredJwtException(cachedClaims.getHeader(), cachedClaims.getBody(), "Token is expired");
            }
            // return valid cached token
            return cachedClaims;
        }
        // parse and validate token
        Jws<Claims> claims = Jwts.parser().setSigningKey(jwtProperties.requirePublicKey()).parseClaimsJws(token);

        // cache token
        jwtCache.put(token, claims);
        return claims;
    }
}
