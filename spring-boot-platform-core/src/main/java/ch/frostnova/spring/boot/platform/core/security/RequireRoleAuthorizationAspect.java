package ch.frostnova.spring.boot.platform.core.security;

import ch.frostnova.spring.boot.platform.api.auth.RequireRole;
import ch.frostnova.spring.boot.platform.api.auth.UserInfo;
import ch.frostnova.spring.boot.platform.api.auth.UserInfoProvider;
import ch.frostnova.spring.boot.platform.api.exception.UnauthenticatedException;
import ch.frostnova.spring.boot.platform.api.exception.UnauthorizedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Aspect to check if the roles in the {@link UserInfo} fulfill the role requirements by {@link RequireRole}
 *
 * @author pwalser
 * @since 2021-11-10
 */
@Aspect
@Component
public class RequireRoleAuthorizationAspect {

    private static final Logger log = LoggerFactory.getLogger(RequireRoleAuthorizationAspect.class);

    @Autowired
    private UserInfoProvider userInfoProvider;

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * Bind aspect to any method annotated by @RequireRole
     *
     * @param joinPoint aspect join point
     * @return invocation result
     * @throws Throwable invocation exception
     */
    @Around("@within(ch.frostnova.spring.boot.platform.api.auth.RequireRole) " +
            "|| @annotation(ch.frostnova.spring.boot.platform.api.auth.RequireRole)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        UserInfo userInfo = userInfoProvider.getUserInfo();
        if (!userInfo.isAuthenticated()) {
            log.warn("Access denied for unauthenticated user");
            throw new UnauthenticatedException();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        String requiredRole = requireRole.value();
        if (requiredRole == null || requiredRole.isBlank()) {
            // no particular role required
            return joinPoint.proceed();
        }
        if (resolveRoles(userInfo.getRoles()).contains(requiredRole)) {
            // user has required role
            return joinPoint.proceed();
        }
        log.warn("Access denied for user {}, required role: '{}'", userInfo, requiredRole);
        throw new UnauthorizedException();
    }
    
    private Set<String> resolveRoles(Collection<String> roleClaims) {
        Map<String, Set<String>> roleMapping = securityProperties.getRoleMapping();
        if (roleMapping.isEmpty()) {
            return roleClaims.stream().collect(toSet());
        }
        return roleClaims.stream().distinct()
                .map(roleClaim -> roleMapping.get(roleClaim))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .collect(toSet());
    }
}