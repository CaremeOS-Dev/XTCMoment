package com.xtc.httplib.cache;

import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.httplib.LogTag;
import com.xtc.httplib.okhttp.BaseInterceptor;
import com.xtc.log.LogUtil;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/** Short-lived cache mapping a request sign to its AES key. */
public class AesKeyCache {

    private static final int LIMIT = 50;
    private static final String TAG = LogTag.tag("AesKeyCache");
    private static final long ONE_MINUTE = TimeUnit.MINUTES.toMillis(1);
    private static volatile AesKeyCache instance = null;

    private final LinkedHashMap<String, CacheData> cache = new LinkedHashMap<>();

    public static AesKeyCache getInstance() {
        if (instance == null) {
            synchronized (AesKeyCache.class) {
                if (instance == null) {
                    instance = new AesKeyCache();
                }
            }
        }
        return instance;
    }

    private AesKeyCache() {
    }

    public synchronized void cache(String sign, String aesKey) {
        if (TextUtils.isEmpty(sign)) {
            LogUtil.d(TAG, "cache: sign is empty");
            return;
        }
        if (this.cache.containsKey(sign)) {
            LogUtil.d(TAG, "cache: sign already cache");
            return;
        }
        checkLimitRemove();
        this.cache.put(sign, new CacheData(SystemClock.elapsedRealtime(), aesKey));
    }

    private void checkLimitRemove() {
        if (this.cache.size() < LIMIT) {
            return;
        }
        Iterator<Map.Entry<String, CacheData>> iterator = this.cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CacheData> entry = iterator.next();
            if (entry == null) {
                iterator.remove();
            } else {
                CacheData data = entry.getValue();
                if (data == null) {
                    iterator.remove();
                } else if (SystemClock.elapsedRealtime() - data.getCacheTime() < ONE_MINUTE) {
                    return;
                } else {
                    iterator.remove();
                }
            }
        }
    }

    /** Returns and removes the AES key for the given sign. */
    public synchronized String getKey(String sign) {
        if (TextUtils.isEmpty(sign)) {
            return BaseInterceptor.ENCRYPT_KEY;
        }
        String aesKey = null;
        if (this.cache.containsKey(sign)) {
            CacheData data = this.cache.get(sign);
            aesKey = data != null ? data.getAesKey() : null;
            this.cache.remove(sign);
        } else {
            LogUtil.d(TAG, "getKey not found : sign = " + sign);
        }
        if (TextUtils.isEmpty(aesKey)) {
            aesKey = BaseInterceptor.ENCRYPT_KEY;
        }
        return aesKey;
    }

    private static class CacheData {
        private String aesKey;
        private long cacheTime;

        public CacheData(long cacheTime, String aesKey) {
            this.cacheTime = cacheTime;
            this.aesKey = aesKey;
        }

        public long getCacheTime() {
            return this.cacheTime;
        }

        public void setCacheTime(long cacheTime) {
            this.cacheTime = cacheTime;
        }

        public String getAesKey() {
            return this.aesKey;
        }

        public void setAesKey(String aesKey) {
            this.aesKey = aesKey;
        }

        @Override
        public String toString() {
            return "CacheData{cacheTime=" + this.cacheTime + ", aesKey=\'" + this.aesKey + "\'}";
        }
    }
}