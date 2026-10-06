package com.xtc.system.wearswitch.function.datacache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 线程安全的数据缓存容器。
 */
public class DataCacheContainer<T, Z> {

    /** 默认容量。 */
    public static final int DEFAULT_CAPACITY = -1;

    private final ConcurrentHashMap<T, Z> cache;

    public DataCacheContainer(int initialCapacity) {
        this.cache = new ConcurrentHashMap<>(initialCapacity);
    }

    /** 写入缓存。 */
    public void put(T key, Z value) {
        if (key == null || value == null) {
            return;
        }
        this.cache.put(key, value);
    }

    public Z get(T key) {
        if (key == null) {
            return null;
        }
        return this.cache.get(key);
    }

    /** 移除缓存。 */
    public void remove(T key) {
        this.cache.remove(key);
    }

    /** 清空缓存。 */
    public void clear() {
        this.cache.clear();
    }
}