package com.xtc.im.core.common.tlv;

import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** TLV 编码/解码/字段缓存，限制最近 20 条。 */
public class TLVCache {

    private static final int MAX_CACHE_SIZE = 20;
    private static List<TLVEncoderCache> tlvEncoderCacheList = new ArrayList<>();
    private static List<TLVDecoderCache> tlvDecoderCacheList = new ArrayList<>();
    private static Map<String, TLVEntityCache> tlvEntityCacheMap = new ConcurrentHashMap<>();

    public static TLVEncodeResult getTLVEncodeResult(int frameType, int dataType, int tagValue, byte[] value) {
        synchronized (tlvEncoderCacheList) {
            Iterator<TLVEncoderCache> iterator = tlvEncoderCacheList.iterator();
            while (iterator.hasNext()) {
                TLVEncodeResult result = iterator.next().get(frameType, dataType, tagValue, value);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    public static void addTlvEncoderCache(int frameType, int dataType, int tagValue, byte[] value,
            TLVEncodeResult result) {
        if (value == null || value.length == 0 || result == null) {
            return;
        }
        TLVEncoderCache cache = new TLVEncoderCache(frameType, dataType, tagValue, value, result);
        synchronized (tlvEncoderCacheList) {
            while (tlvEncoderCacheList.size() > MAX_CACHE_SIZE) {
                tlvEncoderCacheList.remove(0);
            }
            tlvEncoderCacheList.add(cache);
        }
    }

    public static TLVDecodeResult getTLVDecodeResult(byte[] data) {
        synchronized (tlvDecoderCacheList) {
            Iterator<TLVDecoderCache> iterator = tlvDecoderCacheList.iterator();
            while (iterator.hasNext()) {
                TLVDecodeResult result = iterator.next().get(data);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    public static void addTlvDecoderCache(byte[] data, TLVDecodeResult result) {
        if (data == null || data.length == 0 || result == null) {
            return;
        }
        TLVDecoderCache cache = new TLVDecoderCache(data, result);
        synchronized (tlvDecoderCacheList) {
            while (tlvDecoderCacheList.size() > MAX_CACHE_SIZE) {
                tlvDecoderCacheList.remove(0);
            }
            tlvDecoderCacheList.add(cache);
        }
    }

    public static void addTlvEntityCache(String entityName, int tagValue, Field field) {
        if (TextUtils.isEmpty(entityName) || tagValue == 0 || field == null) {
            return;
        }
        TLVEntityCache entityCache = tlvEntityCacheMap.get(entityName);
        if (entityCache == null) {
            entityCache = new TLVEntityCache(entityName);
            tlvEntityCacheMap.put(entityName, entityCache);
        }
        entityCache.putField(tagValue, field);
    }

    public static Field getField(String entityName, int tagValue) {
        if (TextUtils.isEmpty(entityName) || tagValue == 0) {
            return null;
        }
        TLVEntityCache entityCache = tlvEntityCacheMap.get(entityName);
        return entityCache == null ? null : entityCache.getField(tagValue);
    }

    public static int getTagValue(String entityName, String fieldName) {
        if (TextUtils.isEmpty(entityName) || TextUtils.isEmpty(fieldName)) {
            return 0;
        }
        TLVEntityCache entityCache = tlvEntityCacheMap.get(entityName);
        return entityCache == null ? 0 : entityCache.getTagValue(fieldName);
    }

    public static void clear() {
        synchronized (tlvEncoderCacheList) {
            tlvEncoderCacheList.clear();
        }
        synchronized (tlvDecoderCacheList) {
            tlvDecoderCacheList.clear();
        }
        tlvEntityCacheMap.clear();
    }
}