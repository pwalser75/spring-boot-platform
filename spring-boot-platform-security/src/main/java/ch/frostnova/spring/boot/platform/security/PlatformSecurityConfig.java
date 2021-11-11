package ch.frostnova.spring.boot.platform.security;

import ch.frostnova.spring.boot.platform.core.PlatformCoreConfig;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@EnableCaching
@Import(PlatformCoreConfig.class)
public class PlatformSecurityConfig {
}
