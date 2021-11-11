package ch.frostnova.spring.boot.platform.security;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PlatformSecurityConfig.class)
@ComponentScan
public class TestConfig {
}
