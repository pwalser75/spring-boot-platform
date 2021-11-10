package ch.frostnova.spring.boot.platform.core.auth.aspect;

import ch.frostnova.spring.boot.platform.security.api.RequireRole;
import ch.frostnova.spring.boot.platform.security.api.UserInfo;
import ch.frostnova.spring.boot.platform.security.api.UserInfoProvider;
import ch.frostnova.spring.boot.platform.security.api.exception.UnauthenticatedException;
import ch.frostnova.spring.boot.platform.security.api.exception.UnauthorizedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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

    /**
     * Bind aspect to any method annotated by @RequireRole
     *
     * @param joinPoint aspect join point
     * @return invocation result
     * @throws Throwable invocation exception
     */
    @Around("@within(ch.frostnova.spring.boot.platform.security.api.RequireRole) " +
            "|| @annotation(ch.frostnova.spring.boot.platform.security.api.RequireRole)")
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
        if (requiredRole == null || requiredRole.isBlank() || userInfo.getRoles().contains(requiredRole)) {
            return joinPoint.proceed();
        }
        log.warn("Access denied for user {}, required role: '{}'", userInfo, requiredRole);
        throw new UnauthorizedException();
    }

}