package ch.frostnova.spring.boot.platform.core.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

/**
 * A type-safe cache wrapper around the {@link CacheManager}.
 *
 * @author pwalser
 * @since 2021-11-14
 */
public abstract class TypeSafeCache<K extends Serializable, V> {

    private final boolean required;
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private Optional<CacheManager> cacheManager;

    private String cacheName;


    /**
     * Constructor used by subclasses
     *
     * @param cacheName distinguished name of the cache
     */
    protected TypeSafeCache(String cacheName) {
        this(cacheName, false);
    }

    /**
     * Constructor used by subclasses
     *
     * @param cacheName distinguished name of the cache
     * @param required  if true, check on initialization if caching is enabled and the requested cache is available.
     */
    protected TypeSafeCache(String cacheName, boolean required) {
        if (cacheName == null || cacheName.isBlank()) {
            throw new IllegalArgumentException("cacheName is required");
        }
        this.cacheName = cacheName;
        this.required = required;
    }

    @PostConstruct
    private void checkCacheEnabled() {
        boolean enabled = isEnabled();
        if (required && !enabled) {
            logger.error("cache {} is required but unavailable", cacheName);
            throw new IllegalStateException("caching is required but disabled");
        }
        logger.info("cache '{}' {}", cacheName, enabled ? "enabled" : "disabled");
    }

    /**
     * Checks if caching is enabled. When caching is disabled, no values will be put in the cache,
     * returned values are always null, and evict and clear are no-op operations.
     *
     * @return caching enabled
     */
    public boolean isEnabled() {
        return optionalCache().isPresent();
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

    /**
     * Decides whether a particular value should be cached, as by default, only non-null values are cached.
     * Overriding this method allows to cache null-values, or empty collection values, or conditionally cache
     * only special values if requested.
     *
     * @param value value to cache
     * @return whether to cache the value
     */
    protected boolean shouldCacheValue(V value) {
        return value != null;
    }

    /**
     * Internal cache access, caching may be disabled (no cache manager),
     * or the requested cache may not be configured.
     *
     * @return optional cache.
     */
    private Optional<Cache> optionalCache() {
        return cacheManager.map(cm -> cm.getCache(cacheName));
    }

    /**
     * Puts a value in the cache, using the given key. Whether the value is actually cached
     * is decided by the <code>shouldCacheValue(value)</code> method.
     *
     * @param key   key, required
     * @param value value, optional
     */
    public void put(K key, V value) {
        if (!isEnabled()) {
            return;
        }
        if (shouldCacheValue(value)) {
            optionalCache().ifPresent(cache -> cache.put(internalKey(key), value));
        }
    }

    /**
     * Checks if the cache contains an entry for the given key.
     *
     * @param key key, required
     * @return true if value for that key is currently cached.
     */
    public boolean contains(K key) {
        return optionalCache()
                .map(cache -> cache.get(internalKey(key)))
                .isPresent();
    }

    /**
     * Lookup the cached value for the given key. Returns null if not cached, or null was cached.
     *
     * @param key key, required
     * @return potentially cached value.
     */
    @SuppressWarnings("unchecked")
    public V get(K key) {
        return get(key, null);
    }

    /**
     * Lookup the cached value for the given key, or produces and caches it if not yet cached.
     * Returns null if not cached, or null was cached or produced.
     *
     * @param key      key, required
     * @param producer optional producer for missing values.
     * @return potentially cached value.
     */
    @SuppressWarnings("unchecked")
    public V get(K key, Function<K, V> producer) {
        return (V) optionalCache()
                .map(cache -> cache.get(internalKey(key)))
                .map(Cache.ValueWrapper.class::cast)
                .map(Cache.ValueWrapper::get)
                .orElseGet(() -> Optional.ofNullable(producer)
                        .map(p -> p.apply(key))
                        .map(v -> {
                            put(key, v);
                            return v;
                        })
                        .orElse(null));
    }

    /**
     * Evict a value from the cache (if it was present).
     *
     * @param key key, required
     */
    public void evict(K key) {
        optionalCache().ifPresent(cache -> cache.evictIfPresent(internalKey(key)));
    }

    /**
     * Clears the cache, evicting all cached values.
     */
    public void clear() {
        optionalCache().ifPresent(cache -> cache.clear());
    }

    /**
     * Computes the internal cache key.
     *
     * @return internal cache key.
     */
    private Object internalKey(K key) {
        return cacheKey(require(key));
    }

    /**
     * Requires the cache key to be non-null.
     *
     * @param key cache key
     * @return same key
     */
    private K require(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key is required");
        }
        return key;
    }
}
