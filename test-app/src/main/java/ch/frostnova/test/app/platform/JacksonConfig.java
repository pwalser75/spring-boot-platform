package ch.frostnova.test.app.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ch.frostnova.spring.boot.platform.security.jackson.ObjectMappers.json;

/**
 * Jackson configuration.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return json();
    }
}