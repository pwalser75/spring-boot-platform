package ch.frostnova.spring.boot.platform.core.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static java.lang.Integer.MIN_VALUE;

/**
 * Servlet filter which activates the task scope on request level.
 *
 * @author pwalser
 * @since 2019-11-03
 */
@Component
@Order(MIN_VALUE)
public class TaskScopeRequestScopeFilter implements Filter {

    private final static Logger logger = LoggerFactory.getLogger(TaskScopeRequestScopeFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        // unused
    }

    @Override
    public void destroy() {
        // unused
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
            IOException, ServletException {

        if (TaskScope.isActive()) {
            chain.doFilter(request, response);
            return;
        }
        TaskScope.init();
        String conversationId = TaskScope.currentConversationId();
        try {
            logger.debug("Task scope created for request: {}", conversationId);
            chain.doFilter(request, response);
        } finally {
            TaskScope.destroy();
            logger.debug("Task scope destroyed for request: {}", conversationId);
        }
    }
}
