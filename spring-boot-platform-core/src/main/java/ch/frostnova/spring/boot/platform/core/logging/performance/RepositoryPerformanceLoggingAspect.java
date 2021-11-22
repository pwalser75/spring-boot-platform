package ch.frostnova.spring.boot.platform.core.logging.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Aspect
@Component
@ConditionalOnClass(name = "org.springframework.data.repository.Repository")
public class RepositoryPerformanceLoggingAspect {

    /**
     * Bind aspect to intermediate invocations of Spring Data Repositories.
     * Only log such invocations if we passed an entry point already.
     *
     * @param joinPoint aspect join point
     * @return invocation result
     * @throws Throwable invocation exception
     */
    @Around("this(org.springframework.data.repository.Repository)")
    public static Object aroundIntermediate(ProceedingJoinPoint joinPoint) throws Throwable {
        if (PerformanceLoggingContext.current().isIntermediateInvocation()) {
            String invocation = joinPoint.getSignature().toShortString();
            return PerformanceLoggingContext.current().execute(invocation, joinPoint::proceed);
        }
        return joinPoint.proceed();
    }
}
