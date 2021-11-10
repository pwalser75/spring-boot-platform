package ch.frostnova.spring.boot.platform.jwt;

import ch.frostnova.spring.boot.platform.core.PlatformSecurityConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(PlatformSecurityConfig.class)
@ComponentScan
@EnableTransactionManagement
public class TestConfig {
}
