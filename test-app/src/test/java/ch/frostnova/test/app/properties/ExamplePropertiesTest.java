package ch.frostnova.test.app.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

/**
 * Test for {@link ExampleProperties}
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ExamplePropertiesTest {

    private final static double EPSILON = 1e-23;

    @Autowired
    private ExampleProperties exampleProperties;

    @Test
    public void testConfigurationProperties() {

        assertThat(exampleProperties).isNotNull();
        assertThat((Object) exampleProperties).isNotNull();
        assertThat((Integer) exampleProperties.getA()).isEqualTo((Integer) 123);
        assertThat((Double) exampleProperties.getB()).isCloseTo(123.456, offset(EPSILON));
        assertThat((Boolean) exampleProperties.isC()).isTrue();
        assertThat(exampleProperties.getD()).isEqualTo("Test");

        assertThat((Object) exampleProperties.getE()).isNotNull();
        assertThat((Integer) exampleProperties.getE().size()).isEqualTo((Integer) 3);
        assertThat((Boolean) exampleProperties.getE().contains("ONE")).isTrue();
        assertThat((Boolean) exampleProperties.getE().contains("TWO")).isTrue();
        assertThat((Boolean) exampleProperties.getE().contains("THREE")).isTrue();

        assertThat((Object) exampleProperties.getF()).isNotNull();
        assertThat((Integer) exampleProperties.getF().size()).isEqualTo((Integer) 3);
        assertThat(exampleProperties.getF().get(0)).isEqualTo("first");
        assertThat(exampleProperties.getF().get(1)).isEqualTo("second");
        assertThat(exampleProperties.getF().get(2)).isEqualTo("third");
    }

    @Test
    public void testDefaultProperties() {

        assertThat((Object) exampleProperties).isNotNull();
        assertThat((Integer) exampleProperties.getX()).isEqualTo((Integer) 5);
        assertThat((Double) exampleProperties.getY()).isCloseTo(6.7, offset(EPSILON));
        assertThat(exampleProperties.getZ()).isEqualTo("Aloha");
    }
}