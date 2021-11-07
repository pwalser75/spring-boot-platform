package ch.frostnova.test.app;

import ch.frostnova.spring.boot.platform.core.PlatformCoreConfig;
import ch.frostnova.spring.boot.platform.core.PlatformJwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Spring boot application main class
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@Import({PlatformCoreConfig.class, PlatformJwtConfig.class})
public class SpringBootProject {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootProject.class, args);
    }
}