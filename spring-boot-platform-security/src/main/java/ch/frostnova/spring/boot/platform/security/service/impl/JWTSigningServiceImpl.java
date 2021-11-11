package ch.frostnova.spring.boot.platform.security.service.impl;

import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.properties.JwtProperties;
import ch.frostnova.spring.boot.platform.security.service.JWTSigningService;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.UUID;

import static java.util.stream.Collectors.toMap;

@Service
@ConditionalOnProperty(value = "ch.frostnova.platform.security.jwt.private-key")
public class JWTSigningServiceImpl implements JWTSigningService {

    @Autowired
    private Logger logger;

    @Autowired
    private JwtProperties jwtProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @PostConstruct
    public void init() {

        jwtProperties.requirePublicKey();
        logger.warn("{} is activated, service can issue self-signed JWT security tokens - do not use in production", getClass().getSimpleName());
    }

    @Override
    public String createJWT(UserInfo userInfo, OffsetDateTime validFrom, Duration validity) {

        PrivateKey privateKey = jwtProperties.requirePrivateKey();

        return Jwts.builder()
                .setIssuer(Optional.ofNullable(jwtProperties.getIssuer()).filter(Strings::isNotBlank).orElse(applicationName))
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(validFrom.toInstant()))
                .setNotBefore(Date.from(validFrom.toInstant()))
                .setExpiration(Date.from(validFrom.plus(validity).toInstant()))
                .claim(jwtProperties.getClaimTenant(), userInfo.getTenant())
                .setSubject(userInfo.getLogin())
                .claim(jwtProperties.getClaimRoles(), Optional.ofNullable(userInfo.getRoles()).map(TreeSet::new).orElse(null))
                .addClaims(userInfo.getAdditionalClaims().entrySet().stream().collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .signWith(jwtProperties.getSignatureAlgorithm(), jwtProperties.requirePrivateKey())
                .compact();
    }
}
