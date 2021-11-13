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

    private Cache getCache() {
        return cacheManager.orElseThrow(() -> new UnsupportedOperationException("caching is disabled")).getCache(cacheName);
    }

    public void put(K key, V value) {
        checkKey(key);
        if (!isEnabled()) {
            return;
        }
        getCache().put(key, value);
    }

    @SuppressWarnings("unchecked")
    public V get(K key) {
        checkKey(key);
        if (!isEnabled()) {
            return null;
        }
        return (V) Optional.ofNullable(getCache().get(key))
                .map(Cache.ValueWrapper.class::cast)
                .map(Cache.ValueWrapper::get)
                .orElse(null);
    }

    public void evict(K key) {
        checkKey(key);
        if (isEnabled()) {
            getCache().evictIfPresent(key);
        }
    }

    public void clear() {
        if (isEnabled()) {
            getCache().clear();
        }
    }

    private void checkKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is required");
        }
    }
}
