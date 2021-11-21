package ch.frostnova.spring.boot.platform.jwt;

import ch.frostnova.spring.boot.platform.core.PlatformCoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(PlatformCoreConfig.class)
public class TestBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestBootApplication.class, args);
    }
}