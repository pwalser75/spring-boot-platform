package ch.frostnova.spring.boot.platform.security.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Servlet filter logging HTTP access (method, URI) with response code and execution time.
 *
 * @author pwalser
 * @since 2011-08-17
 */
@Component
@Order(1)
@ConditionalOnProperty(value = "ch.frostnova.platform.logging.access-log.enabled", havingValue = "true")
public class AccessLogFilter implements Filter {

    private final static double NS_TO_MS_FACTOR = 1e-6;
    private final static Logger logger = LoggerFactory.getLogger(AccessLogFilter.class);

    private static String formatDuration(long durationNs) {
        return BigDecimal.valueOf(durationNs * NS_TO_MS_FACTOR).setScale(2, RoundingMode.HALF_UP).toString();
    }

    @Override
    public void init(FilterConfig filterConfig) {
        // unused
    }

    @Override
    public void destroy() {
        // unused
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        long startTime = System.nanoTime();

        chain.doFilter(request, response);

        long durationNs = System.nanoTime() - startTime;

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            String method = httpServletRequest.getMethod();
            String uri = httpServletRequest.getRequestURI();
            int responseStatusCode = httpServletResponse.getStatus();
            String responseStatus = Optional.ofNullable(HttpStatus.resolve(responseStatusCode)).map(HttpStatus::name).orElse("unknown");
            String duration = formatDuration(durationNs);

            MDC.put(MDCConstants.ACCESS_LOG_METHOD.name(), method);
            MDC.put(MDCConstants.ACCESS_LOG_URI.name(), uri);
            MDC.put(MDCConstants.ACCESS_LOG_STATUS.name(), responseStatus);
            MDC.put(MDCConstants.ACCESS_LOG_DURATION_MS.name(), duration);

            logger.info("{} {} -> {} {}, {} ms", method, uri, responseStatusCode, responseStatus, duration);
        }
    }

    enum MDCConstants {
        ACCESS_LOG_METHOD,
        ACCESS_LOG_URI,
        ACCESS_LOG_STATUS,
        ACCESS_LOG_DURATION_MS
    }
}