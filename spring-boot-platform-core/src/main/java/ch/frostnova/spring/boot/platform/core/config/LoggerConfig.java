package ch.frostnova.spring.boot.platform.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Allows {@link Logger} to be injected using <code>@Autowired</code>.
 *
 * @author pwalser
 * @since 2011-08-17
 */
@Configuration
public class LoggerConfig {

    @Bean
    @Scope(BeanDefinition.SCOPE_PROTOTYPE)
    public Logger logger(InjectionPoint injectionPoint) {
        return LoggerFactory.getLogger(injectionPoint.getField().getDeclaringClass());
    }
}
