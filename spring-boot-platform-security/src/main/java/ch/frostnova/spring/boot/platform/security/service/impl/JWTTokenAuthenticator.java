package ch.frostnova.spring.boot.platform.security.service.impl;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.properties.JwtProperties;
import ch.frostnova.spring.boot.platform.security.properties.SecurityProperties;
import ch.frostnova.spring.boot.platform.security.service.JWTVerificationService;
import ch.frostnova.spring.boot.platform.security.service.TokenAuthenticator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

@Component
@ConditionalOnProperty(value = "ch.frostnova.platform.security.jwt.enabled", havingValue = "true")
public class JWTTokenAuthenticator implements TokenAuthenticator {

    private Set<String> reservedClaims;

    @Autowired
    private Logger logger;

    @Autowired
    private JWTVerificationService jwtVerificationService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private SecurityProperties securityProperties;

    @PostConstruct
    private void init() {
        reservedClaims = Set.of("sub", jwtProperties.getClaimTenant(), jwtProperties.getClaimRoles());
    }

    @Override
    public UserInfo authenticate(String token) throws SecurityException {

        logger.debug("Token: {}", token);
        Jws<Claims> claims = jwtVerificationService.verify(token);
        Claims body = claims.getBody();

        logger.debug("Authenticated as: {}", body);

        List<?> rawRoleClaims = body.get(jwtProperties.getClaimRoles(), List.class);
        Set<String> roleClaims = Optional.ofNullable(rawRoleClaims.stream().map(String::valueOf).collect(toSet())).orElse(emptySet());
        Map<String, String> additionalClaims = new HashMap<>();
        body.forEach((key, value) -> {
            if (!reservedClaims.contains(key)) {
                additionalClaims.put(key, toString(value));
            }
        });

        return UserInfo.userInfo(body.getSubject())
                .tenant(body.get(jwtProperties.getClaimTenant(), String.class))
                .roles(roleClaims)
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
