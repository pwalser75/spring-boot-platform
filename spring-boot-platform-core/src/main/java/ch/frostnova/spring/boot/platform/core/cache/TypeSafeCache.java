package ch.frostnova.spring.boot.platform.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.Serializable;
import java.util.Optional;

/**
 * A type-safe cache wrapper around the {@link CacheManager}.
 */
public abstract class TypeSafeCache<K extends Serializable, V> {

    @Autowired
    private Optional<CacheManager> cacheManager;

    private String cacheName;

    public TypeSafeCache(String cacheName) {
        if (cacheName == null || cacheName.isBlank()) {
            throw new IllegalArgumentException("cacheName is required");
        }
        this.cacheName = cacheName;
    }

    public boolean isEnabled() {
        return cacheManager.isPresent();
    }

    /**
     * Calculates the internal cache key for the provided key. By default, the internal key is the same as the external.
     * For more compact keys (which use less memory, and are faster to check for equality), this method can be overridden.
     *
     * @param key external key (used when working with this cache), never null
     * @return internally used cache key, never null
     */
    protected Object cacheKey(K key) {
        return key;
    }

    private Cache getCache() {
        return cacheManager.orElseThrow(() -> new UnsupportedOperationException("caching is disabled")).getCache(cacheName);
    }

    public void put(K key, V value) {
        if (!isEnabled()) {
            return;
        }
        getCache().put(internalKey(key), value);
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        if (!isEnabled()) {
            return null;
        }
        return (V) Optional.ofNullable(getCache().get(internalKey(key)))
                .map(Cache.ValueWrapper.class::cast)
                .map(Cache.ValueWrapper::get)
                .orElse(null);
    }

    public void evict(K key) {
        if (isEnabled()) {
            getCache().evictIfPresent(internalKey(key));
        }
    }

    public void clear() {
        if (isEnabled()) {
            getCache().clear();
        }
    }

    private Object internalKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is required");
        }
        Object cacheKey = cacheKey(key);
        return cacheKey;
    }
}
