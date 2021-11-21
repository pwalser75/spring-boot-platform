package ch.frostnova.spring.boot.platform.jwt.service.impl;

import ch.frostnova.spring.boot.platform.api.auth.AuthenticationProvider;
import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.jwt.properties.JwtProperties;
import ch.frostnova.spring.boot.platform.jwt.properties.SecurityProperties;
import ch.frostnova.spring.boot.platform.jwt.service.JWTVerificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

@Component
@ConditionalOnProperty(value = "ch.frostnova.platform.security.jwt.enabled", havingValue = "true")
public class JWTTokenAuthenticator implements AuthenticationProvider {

    private final static Logger logger = LoggerFactory.getLogger(JWTTokenAuthenticator.class);

    private final static Pattern JWT_PATTERN = Pattern.compile("^[\\w-]+\\.[\\w-]+\\.[\\w-]+$");

    @Autowired
    private JWTVerificationService jwtVerificationService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private ObjectMapper objectMapper;

    private Set<String> reservedClaims;

    @PostConstruct
    private void init() {
        reservedClaims = Set.of("sub", jwtProperties.getClaimTenant(), jwtProperties.getClaimRoles());
    }

    @Override
    public UserInfo authenticate(String authorization) throws SecurityException {

        String token = getBearerToken(authorization);
        if (token == null) {
            // not a Bearer token, pass (could be another token, handled by another authentication provider).
            return null;
        }
        Matcher matcher = JWT_PATTERN.matcher(token);
        if (!matcher.matches()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not a JWT: " + token);
            }
            // not a JWT, pass (could be another bearer token, handled by another authentication provider).
            return null;
        }

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
