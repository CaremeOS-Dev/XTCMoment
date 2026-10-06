package com.xtc.assistantapi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xtc.assistantapi.message.Directive;
import com.xtc.assistantapi.message.DirectiveDeserializer;

import java.lang.reflect.Type;

/**
 * 指令 Gson 工具，注册了自定义的指令反序列化器。
 */
public class DirectiveGsonUtil {

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
        return new GsonBuilder().registerTypeAdapter(Directive.class, new DirectiveDeserializer()).create();
    }
}