package ch.frostnova.spring.boot.platform.core.cache;

import java.util.Base64;
import java.util.UUID;

import static ch.frostnova.spring.boot.platform.core.util.StringUtils.md5;

public class UUIDCache extends TypeSafeCache<Integer, UUID> {

    private final static String CACHE_NAME = "uuid-cache";

    public UUIDCache() {
        super(CACHE_NAME);
    }

    @Override
    protected Object cacheKey(Integer key) {
        return hashedKey(String.valueOf(key));
    }

    private String hashedKey(String key) {
        return Base64.getEncoder().encodeToString(md5(key));
    }

    @Override
    protected boolean shouldCacheValue(UUID value) {
        return true;
    }
}
