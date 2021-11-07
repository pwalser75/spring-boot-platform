package ch.frostnova.spring.boot.platform.jwt;

import ch.frostnova.spring.boot.platform.core.PlatformJwtConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PlatformJwtConfig.class)
public class TestConfig {
}
