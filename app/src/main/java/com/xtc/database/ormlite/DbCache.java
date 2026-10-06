package com.xtc.database.ormlite;

import android.text.TextUtils;
import android.util.LruCache;

import com.xtc.log.LogUtil;

import java.util.HashMap;
import java.util.Map;

/** Small LRU cache of the per-table string values. */
public class DbCache {

    private static final String TAG = "DbCache";
    private static final int MAX_SIZE = 100;

    private static DbCache instance;
    private LruCache<String, Map<String, String>> cache;

    private DbCache() {
    }

    public static DbCache getInstance() {
        if (instance == null) {
            synchronized (DbCache.class) {
                if (instance == null) {
                    instance = new DbCache();
                }
            }
        }
        return instance;
    }

    private synchronized void ensureCache() {
        this.cache = new LruCache<String, Map<String, String>>(MAX_SIZE) {
            @Override
            protected int sizeOf(String key, Map<String, String> value) {
                return value.size();
            }
        };
    }

    /** Stores [value] under the {@code tableName}/{@code key} pair. */
    public <T> void put(String tableName, String key, T value) {
        if (this.cache == null) {
            ensureCache();
        }
        if (isInvalid(tableName) || isInvalid(key) || value == null) {
            LogUtil.i(TAG, "value is null");
            return;
        }
        Map<String, String> tableCache = this.cache.remove(tableName);
        if (tableCache == null) {
            tableCache = new HashMap<>();
        }
        tableCache.put(key, String.valueOf(value));
        this.cache.put(tableName, tableCache);
    }

    /** @return the value stored under the {@code tableName}/{@code key} pair, or null. */
    public String get(String tableName, String key) {
        if (isInvalid(tableName) || isInvalid(key)) {
            LogUtil.i(TAG, "mLruCache is empty");
            return null;
        }
        Map<String, String> tableCache = this.cache.get(tableName);
        if (tableCache == null || tableCache.isEmpty()) {
            LogUtil.i(TAG, "tableCache is empty");
            return null;
        }
        return tableCache.get(key);
    }

    /** Removes the value stored under the {@code tableName}/{@code key} pair. */
    public void remove(String tableName, String key) {
        if (isInvalid(tableName) || isInvalid(key)) {
            return;
        }
        Map<String, String> tableCache = this.cache.get(tableName);
        if (tableCache == null) {
            return;
        }
        tableCache.remove(key);
    }

    /** Removes every entry of [tableName]. */
    public void removeTable(String tableName) {
        if (isInvalid(tableName)) {
            return;
        }
        this.cache.remove(tableName);
    }

    /** Empties the cache. */
    public void clear() {
        LruCache<String, Map<String, String>> lruCache = this.cache;
        if (lruCache != null && lruCache.size() > 0) {
            this.cache.evictAll();
        }
        System.gc();
    }

    private boolean isInvalid(String key) {
        if (this.cache == null) {
            return true;
        }
        return TextUtils.isEmpty(key);
    }
}