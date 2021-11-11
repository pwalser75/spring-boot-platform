package ch.frostnova.spring.boot.platform.security.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

/**
 * A type-safe cache wrapper around the {@link CacheManager}.
 */
@Component
public class TypeSafeCache {

    @Autowired
    private CacheManager cacheManager;

    public <K extends Serializable, V> void put(String cacheName, K key, V value) {
        checkCacheName(cacheName);
        checkKey(key);
        cacheManager.getCache(cacheName).put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <K extends Serializable, V> V get(String cacheName, K key) {
        checkCacheName(cacheName);
        checkKey(key);
        return (V) Optional.ofNullable(cacheManager.getCache(cacheName).get(key))
                .map(Cache.ValueWrapper.class::cast)
                .map(Cache.ValueWrapper::get)
                .orElse(null);
    }

    public <K extends Serializable> void evict(String cacheName, K key) {
        checkCacheName(cacheName);
        checkKey(key);
        cacheManager.getCache(cacheName).evictIfPresent(key);
    }

    public void clear(String cacheName) {
        checkCacheName(cacheName);
        cacheManager.getCache(cacheName).clear();
    }

    private void checkCacheName(String cacheName) {
        if (cacheName == null) {
            throw new IllegalArgumentException("cacheName is required");
        }
    }

    private <K extends Serializable> void checkKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is required");
        }
    }
}
