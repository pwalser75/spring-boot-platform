package ch.frostnova.spring.boot.platform.core.auth.impl;

import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.core.auth.JWTVerificationService;
import ch.frostnova.spring.boot.platform.core.auth.TokenAuthenticator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toSet;

@Component
@ConditionalOnProperty(value = "ch.frostnova.platform.security.auth", havingValue = "jwt")
public class JWTTokenAuthenticator implements TokenAuthenticator {

    private final static Set<String> RESERVED_CLAIMS = Set.of("sub", "tenant", "scope");

    @Autowired
    private Logger logger;

    @Autowired
    private JWTVerificationService jwtVerificationService;

    @Override
    public UserInfo authenticate(String token) throws SecurityException {

        logger.debug("Token: {}", token);
        Jws<Claims> claims = jwtVerificationService.verify(token);
        Claims body = claims.getBody();

        logger.debug("Authenticated as: {}", body);

        List<?> scopes = body.get("scope", List.class);
        Map<String, String> additionalClaims = new HashMap<>();
        body.forEach((key, value) -> {
            if (!RESERVED_CLAIMS.contains(key)) {
                additionalClaims.put(key, toString(value));
            }
        });

        return UserInfo.userInfo(body.getSubject())
                .tenant(body.get("tenant", String.class))
                .roles(scopes.stream().map(String::valueOf).collect(toSet()))
                .additionalClaims(additionalClaims)
                .build();
    }

    private String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Iterable<?>) {
            Iterable<?> iterable = (Iterable<?>) obj;
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(this::toString)
                    .collect(Collectors.joining(","));
        }
        return String.valueOf(obj);
    }
}
