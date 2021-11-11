package ch.frostnova.spring.boot.platform.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(PlatformCoreConfig.class)
public class TestConfig {
}
