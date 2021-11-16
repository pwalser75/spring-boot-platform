package ch.frostnova.spring.boot.platform.core.cache;

import ch.frostnova.spring.boot.platform.core.config.CacheConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.awt.Point;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the {@link TypeSafeCache}
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CacheConfig.class, GeometryCache.class, UUIDCache.class})
public class TypeSafeCacheTest {

    @Autowired
    private UUIDCache uuidCache;

    @Autowired
    private GeometryCache geometryCache;

    @BeforeEach
    void setup() {
        uuidCache.clear();
        geometryCache.clear();
    }

    @Test
    void shouldUseKeyAsDefaultInternalCacheKey() {
        assertThat(geometryCache.cacheKey(new Point(1, 2))).isEqualTo(new Point(1, 2));
        assertThat(geometryCache.cacheKey(new Point(3, 4))).isEqualTo(new Point(3, 4));
    }

    @Test
    void shouldDeriveInternalCacheKey() {
        assertThat(uuidCache.cacheKey(123)).isEqualTo("ICy5YqxZB1uWSwcVLSNLcA==");
        assertThat(uuidCache.cacheKey(456)).isEqualTo("JQz4tRx3Pz+NyLS+hnqaAg==");
    }

    @Test
    void shouldCacheEmptyValue() {
        uuidCache.put(123, null);

        assertThat(uuidCache.contains(123)).isTrue();
        assertThat(uuidCache.contains(456)).isFalse();
        assertThat(uuidCache.get(123)).isNull();
        assertThat(uuidCache.get(456)).isNull();
    }

    @Test
    void shouldNotCacheEmptyValue() {
        geometryCache.put(new Point(1, 2), null);

        assertThat(geometryCache.contains(new Point(1, 2))).isFalse();
        assertThat(geometryCache.contains(new Point(3, 4))).isFalse();
        assertThat(geometryCache.get(new Point(1, 2))).isNull();
        assertThat(geometryCache.get(new Point(3, 4))).isNull();
    }

    @Test
    void shouldCacheValue() {
        UUID value1 = UUID.randomUUID();
        UUID value2 = UUID.randomUUID();
        uuidCache.put(123, value1);
        uuidCache.put(456, value2);
        assertThat(uuidCache.contains(123)).isTrue();
        assertThat(uuidCache.contains(456)).isTrue();
        assertThat(uuidCache.contains(789)).isFalse();
        assertThat(uuidCache.get(123)).isSameAs(value1);
        assertThat(uuidCache.get(456)).isSameAs(value2);

        Double area1 = 2.0;
        Double area2 = Math.random();
        geometryCache.put(new Point(1, 2), area1);
        geometryCache.put(new Point(3, 4), area2);
        assertThat(geometryCache.contains(new Point(1, 2))).isTrue();
        assertThat(geometryCache.contains(new Point(3, 4))).isTrue();
        assertThat(geometryCache.contains(new Point(5, 6))).isFalse();
        assertThat(geometryCache.get(new Point(1, 2))).isSameAs(area1);
        assertThat(geometryCache.get(new Point(3, 4))).isSameAs(area2);
    }

    @Test
    void shouldProduceAndCacheValue() {
        UUID value = UUID.randomUUID();

        assertThat(uuidCache.contains(123)).isFalse();
        assertThat(uuidCache.get(123, k -> value)).isSameAs(value);
        assertThat(uuidCache.contains(123)).isTrue();
        assertThat(uuidCache.get(123)).isSameAs(value);
        assertThat(uuidCache.get(123, k -> UUID.randomUUID())).isSameAs(value);

        assertThat(geometryCache.contains(new Point(3, 4))).isFalse();
        Double area = geometryCache.get(new Point(3, 4), k -> (double) k.x * k.y);
        assertThat(area).isEqualTo(Double.valueOf(12));
        assertThat(geometryCache.contains(new Point(3, 4))).isTrue();
        assertThat(geometryCache.get(new Point(3, 4))).isSameAs(area);
    }

    @Test
    void shouldEvictValue() {
        UUID value1 = UUID.randomUUID();
        UUID value2 = UUID.randomUUID();
        uuidCache.put(123, value1);
        uuidCache.put(456, value2);
        assertThat(uuidCache.get(123)).isSameAs(value1);
        assertThat(uuidCache.get(456)).isSameAs(value2);

        uuidCache.evict(123);
        uuidCache.evict(789);
        assertThat(uuidCache.contains(123)).isFalse();
        assertThat(uuidCache.contains(456)).isTrue();
        assertThat(uuidCache.contains(789)).isFalse();
        assertThat(uuidCache.get(123)).isNull();
        assertThat(uuidCache.get(456)).isNotNull().isSameAs(value2);
    }

    @Test
    void shouldClearCache() {
        uuidCache.put(123, UUID.randomUUID());
        uuidCache.put(456, UUID.randomUUID());
        geometryCache.put(new Point(5, 6), 30d);

        uuidCache.clear();
        assertThat(uuidCache.contains(123)).isFalse();
        assertThat(uuidCache.contains(456)).isFalse();
        assertThat(uuidCache.contains(456)).isFalse();
        assertThat(uuidCache.get(123)).isNull();
        assertThat(uuidCache.get(456)).isNull();

        assertThat(geometryCache.contains(new Point(5, 6))).isTrue();
        geometryCache.clear();
        assertThat(geometryCache.contains(new Point(5, 6))).isFalse();
        assertThat(geometryCache.get(new Point(5, 6))).isNull();
    }
}
