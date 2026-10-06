package com.xtc.bigdata.common.utils;

/**
 * 大数据模块使用的 JSON 转换入口，转发到通用 JSON 工具。
 */
public class JSONUtil {

    public static String toJSON(Object object) {
        return com.xtc.utils.encode.JSONUtil.toJSON(object);
    }

    public static <T> T fromJSON(String json, Class<T> classType) {
        return com.xtc.utils.encode.JSONUtil.fromJSON(json, classType);
    }

    public static <T> T toCollection(String json, Class<?> rawType, Class<?>... typeArguments) {
        return com.xtc.utils.encode.JSONUtil.fromJSON(json, rawType, typeArguments);
    }

    public static Object get(String json, String key) {
        return com.xtc.utils.encode.JSONUtil.getJSONValue(json, key);
    }
}