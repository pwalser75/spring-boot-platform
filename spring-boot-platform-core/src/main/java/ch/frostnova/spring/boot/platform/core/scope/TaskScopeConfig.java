package ch.frostnova.spring.boot.platform.core.scope;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registers the {@link TaskScope} as scope in the {@link BeanFactory}.
 *
 * @author pwalser
 * @since 2019-11-03
 */
@Configuration
public class TaskScopeConfig {

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return beanFactory -> beanFactory.registerScope(TaskScope.NAME, new TaskScope());
    }
}
