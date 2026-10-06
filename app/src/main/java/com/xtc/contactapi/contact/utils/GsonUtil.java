package com.xtc.contactapi.contact.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * Gson 序列化工具，统一使用序列化 null 字段的配置。
 */
public class GsonUtil {

    public static String toJson(Object object) {
        return newGson().toJson(object);
    }

    public static String toJson(Object object, Type type) {
        return newGson().toJson(object, type);
    }

    public static <T> T fromJson(String json, Class<T> classType) {
        try {
            return newGson().fromJson(json, classType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(String json, Type type) {
        try {
            return newGson().fromJson(json, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Gson newGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        return builder.create();
    }
}