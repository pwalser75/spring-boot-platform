package ch.frostnova.spring.boot.platform.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PlatformCoreConfig.class)
public class TestConfig {
}
