package ch.frostnova.spring.boot.platform.core.logging.performance;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Performance logging aspect, logs performance and result state (ok or exception) for (nested) service calls.<br> Activated by profile
 * <code>performance-logging</code>, the aspect will log performance for any
 * <ul>
 * <li>classes annotated with <code>@PerformanceLogging</code></li>
 * <li>Spring <code>@Service</code></li>
 * <li>Spring <code>@Controller</code></li>
 * <li>Spring <code>@RestController</code></li>
 * <li>Spring Data <code>Repository</code></li>
 * </ul>
 * Example log output:
 * <pre><code>
 * 15:12:22.316 INFO  [main] | PerformanceLoggingAspect - Test.a() -&gt; 211.20 ms, self: 51.30 ms
 * &nbsp;&nbsp;+ Test.b() -&gt; java.lang.IllegalArgumentException, 134.04 ms, self: 102.04 ms
 * &nbsp;&nbsp;&nbsp;&nbsp;+ 5x Test.c() -&gt; 51.94 ms
 * &nbsp;&nbsp;&nbsp;&nbsp;+ Test.d() -&gt; java.lang.ArithmeticException, 0.03 ms
 * &nbsp;&nbsp;+ Test.e() -&gt; 25.86 ms
 * 15:12:22.339 INFO  [main] | PerformanceLoggingAspect - Other.x() -&gt; 12.55 ms, self: 2.57 ms
 * &nbsp;&nbsp;+ Other.y() -&gt; 9.98 ms, self: 7.92 ms
 * &nbsp;&nbsp;&nbsp;&nbsp;+ Other.z() -&gt; 2.06 ms
 * </code></pre>
 *
 * @author pwalser
 * @since 2018-11-02
 */
@Aspect
@Component
public class PerformanceLoggingAspect {

    /**
     * Bind aspect to any Spring @PerformanceLogging, @Controller, @RestController and @Scheduled
     *
     * @param joinPoint aspect join point
     * @return invocation result
     */
    @Around("@within(ch.frostnova.spring.boot.platform.api.logging.PerformanceLogging)" +
            "|| @annotation(ch.frostnova.spring.boot.platform.api.logging.PerformanceLogging)" +
            "|| @within(org.springframework.stereotype.Controller)" +
            "|| @within(org.springframework.web.bind.annotation.RestController)" +
            "|| @within(org.springframework.scheduling.annotation.Scheduled)"
    )
    public static Object around(ProceedingJoinPoint joinPoint) {
        String invocation = joinPoint.getSignature().toShortString();
        return PerformanceLoggingContext.current().execute(invocation, joinPoint::proceed);
    }

    /**
     * Bind aspect to intermediate invocations of @Service and @Component. Only log such invocations if we passed an entry point already.
     *
     * @param joinPoint aspect join point
     * @return invocation result
     * @throws Throwable invocation exception
     */
    @Around("@within(ch.frostnova.spring.boot.platform.api.logging.PerformanceLogging)" +
            "|| @annotation(ch.frostnova.spring.boot.platform.api.logging.PerformanceLogging)" +
            "|| @within(org.springframework.stereotype.Service)" +
            "|| @within(org.springframework.stereotype.Component)")
    public static Object aroundIntermediate(ProceedingJoinPoint joinPoint) throws Throwable {
        if (PerformanceLoggingContext.current().isIntermediateInvocation()) {
            String invocation = joinPoint.getSignature().toShortString();
            return PerformanceLoggingContext.current().execute(invocation, joinPoint::proceed);
        }
        return joinPoint.proceed();
    }
}
