package ch.frostnova.spring.boot.platform.jwt.service;

import ch.frostnova.spring.boot.platform.jwt.TestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("ec-keys")
@SpringBootTest(classes = {TestConfig.class})
@EnableConfigurationProperties
public class SigningServiceTestEC extends SigningServiceTest {

}
