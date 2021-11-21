package ch.frostnova.spring.boot.platform.jwt;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PlatformJwtExtensionConfig.class)
@ComponentScan
public class TestConfig {
}
