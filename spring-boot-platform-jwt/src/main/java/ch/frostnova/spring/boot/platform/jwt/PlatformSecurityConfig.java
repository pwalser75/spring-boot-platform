package ch.frostnova.spring.boot.platform.jwt;

import ch.frostnova.spring.boot.platform.core.PlatformCoreConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(PlatformCoreConfig.class)
public class PlatformSecurityConfig {
}
