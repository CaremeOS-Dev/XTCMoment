package com.xtc.web.core.utils;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;

/** Web 组件专用的 Gson 封装，兼容 JS 侧传来的时间戳、布尔、字节数组等特殊格式。 */
public class JSONUtil {

    private static String TAG = JSONUtil.class.getName();
    private static Gson gson = getGsonBuilder().create();

    /** 构造带自定义 TypeAdapter 的 GsonBuilder。 */
    public static GsonBuilder getGsonBuilder() {
        GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        builder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
                return new JsonPrimitive(Long.valueOf(date.getTime()));
            }
        });
        builder.registerTypeAdapter(Boolean.TYPE, new JsonDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return Boolean.valueOf(json.getAsInt() != 0);
                } catch (NumberFormatException e) {
                    return Boolean.valueOf(json.getAsBoolean());
                }
            }
        });
        builder.registerTypeAdapter(Boolean.class, new JsonDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return Boolean.valueOf(json.getAsInt() != 0);
                } catch (NumberFormatException e) {
                    return Boolean.valueOf(json.getAsBoolean());
                }
            }
        });
        builder.registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
            @Override
            public JsonElement serialize(byte[] data, Type type, JsonSerializationContext context) {
                return new JsonPrimitive(Base64.encodeToString(data, Base64.NO_WRAP));
            }
        });
        builder.registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
            @Override
            public byte[] deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return Base64.decode(json.getAsString(), Base64.NO_WRAP);
                } catch (IllegalStateException e) {
                    JsonArray array = json.getAsJsonArray();
                    byte[] data = new byte[array.size()];
                    for (int index = 0; index < array.size(); index++) {
                        data[index] = array.get(index).getAsByte();
                    }
                    return data;
                }
            }
        });
        builder.registerTypeAdapter(Integer.TYPE, new JsonDeserializer<Integer>() {
            @Override
            public Integer deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                    throws JsonParseException {
                return Integer.valueOf(json.getAsInt());
            }
        });
        return builder;
    }

    /** 对象转 JSON 字符串，失败时返回 null。 */
    public static String toJSON(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            Log.e(TAG, "toJSON failed with obj=" + object, e);
            return null;
        }
    }

    /** JSON 字符串转对象，失败时返回 null。 */
    public static <T> T fromJSON(String json, Class<T> classType) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return gson.fromJson(json, classType);
        } catch (Exception e) {
            Log.e(TAG, "fromJSON failed with jsonStr=" + json + ", classType=" + classType, e);
            return null;
        }
    }

    /** JSON 字符串转泛型集合。 */
    public static <T> T toCollection(String json, Class<?> rawType, Class<?>... typeArguments) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return gson.fromJson(json, TypeToken.getParameterized(rawType, typeArguments).getType());
        } catch (Exception e) {
            Log.e(TAG, "toCollection failed with jsonStr=" + json + ", collectionClass=" + rawType, e);
            return null;
        }
    }

    /** 取 JSON 中某个字段的原始值。 */
    public static Object get(String json, String key) {
        try {
            return new JSONObject(json).get(key);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}