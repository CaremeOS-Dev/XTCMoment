package com.xtc.contactapi.contacthead.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.LruCache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 基于 LruCache 的缓存工具，缓存值为 BitmapDrawable 时按位图字节数计算大小。
 */
public class LruCacheUtil<K, V> {

    private final String tag = LruCacheUtil.class.getSimpleName();
    private final ReferenceQueue<V> referenceQueue = new ReferenceQueue<>();

    private volatile LruCache<K, V> lruCache;
    private final int cacheSize;
    private CacheChangeListener<K, V> cacheChangeListener;

    /** 缓存变化监听。 */
    public interface CacheChangeListener<K, V> {
        /** 因容量淘汰。 */
        void onEntryEvicted(K key, V oldValue, V newValue);

        /** 因显式移除。 */
        void onEntryRemoved(K key, V oldValue, V newValue);
    }

    public LruCacheUtil(int cacheSize) {
        this.cacheSize = cacheSize;
        initCache();
    }

    public LruCacheUtil(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        this.cacheSize = getDefaultCacheMemorySize(context);
        initCache();
    }

    public CacheChangeListener<K, V> getCacheChangeListener() {
        return cacheChangeListener;
    }

    public void setCacheChangeListener(CacheChangeListener<K, V> listener) {
        this.cacheChangeListener = listener;
    }

    private void initCache() {
        this.lruCache = new LruCache<K, V>(cacheSize) {
            @Override
            protected int sizeOf(K key, V value) {
                if (value instanceof BitmapDrawable) {
                    BitmapDrawable drawable = (BitmapDrawable) value;
                    if (drawable.getBitmap() == null) {
                        return 0;
                    }
                    return drawable.getBitmap().getByteCount();
                }
                return super.sizeOf(key, value);
            }

            @Override
            protected synchronized void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
                if (cacheChangeListener == null) {
                    return;
                }
                if (evicted) {
                    cacheChangeListener.onEntryEvicted(key, oldValue, newValue);
                } else {
                    cacheChangeListener.onEntryRemoved(key, oldValue, newValue);
                }
                new WeakReference<>(oldValue, referenceQueue);
            }
        };
    }

    private int getDefaultCacheMemorySize(Context context) {
        if (context == null) {
            return 0;
        }
        int size = (((ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1048576) / 8;
        Log.i(tag, "getDefaultCacheMemorySize:" + (size / 1024) / 1024 + "  MB ");
        return size;
    }

    /** 写入缓存，返回是否覆盖了已有值。 */
    public boolean put(K key, V value) {
        synchronized (LruCache.class) {
            V previous = lruCache.put(key, value);
            Log.i(tag, "拥有" + lruCache.snapshot().size() + "个缓存，占用:" + (lruCache.size() / 1024) + " KB");
            return previous != null;
        }
    }

    public V get(K key) {
        synchronized (LruCache.class) {
            return lruCache.get(key);
        }
    }

    /** 判断缓存中是否已存在等价的值。 */
    public boolean containsEqualValue(K key, V value) {
        synchronized (LruCache.class) {
            V previous = lruCache.get(key);
            V putResult = lruCache.put(key, value);
            if (previous != null && putResult != null) {
                if (previous.hashCode() != putResult.hashCode() && !previous.equals(putResult)) {
                    return false;
                }
                return true;
            }
            return false;
        }
    }

    public boolean remove(K key) {
        synchronized (LruCache.class) {
            return lruCache.remove(key) != null;
        }
    }

    public boolean evictAll() {
        synchronized (LruCache.class) {
            lruCache.evictAll();
        }
        return true;
    }

    public boolean containsKey(K key) {
        synchronized (LruCache.class) {
            return lruCache.snapshot().containsKey(key);
        }
    }

    /** 返回 key 中包含指定片段的全部缓存 key。 */
    public List<K> findKeysContaining(K keyPart) {
        synchronized (LruCache.class) {
            List<K> matchedKeys = new ArrayList<>();
            for (K key : lruCache.snapshot().keySet()) {
                if ((key instanceof String) && ((String) key).contains((String) keyPart)) {
                    matchedKeys.add(key);
                }
            }
            return matchedKeys;
        }
    }

    public Set<K> keySet() {
        return lruCache.snapshot().keySet();
    }
}