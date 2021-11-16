package ch.frostnova.spring.boot.platform.core.cache;

import java.awt.Point;

public class GeometryCache extends TypeSafeCache<Point, Double> {

    private final static String CACHE_NAME = "geometry-cache";

    public GeometryCache() {
        super(CACHE_NAME);
    }
}
