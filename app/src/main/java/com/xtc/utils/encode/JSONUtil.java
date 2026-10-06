package com.xtc.utils.encode;

import android.text.TextUtils;
import android.util.Base64;

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
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.xtc.log.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Shared JSON facade over Gson.
 *
 * <p>Registers the adapters the app relies on: {@code Date} as epoch millis,
 * {@code boolean} tolerant of 0/1, {@code byte[]} as Base64, and {@code Integer}
 * from a numeric node. Optionally wires the kotlinx-serialization adapter factory
 * if it is on the classpath.
 */
public class JSONUtil {

    private static final String TAG = JSONUtil.class.getName();
    private static final Gson GSON = newGsonBuilder().create();

    public static GsonBuilder newGsonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder().disableHtmlEscaping();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
            }
        });
        gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date date, Type type, JsonSerializationContext context) {
                return new JsonPrimitive((Number) Long.valueOf(date.getTime()));
            }
        });
        gsonBuilder.registerTypeAdapter(Boolean.TYPE, new JsonDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return Boolean.valueOf(jsonElement.getAsInt() != 0);
                } catch (NumberFormatException e) {
                    return Boolean.valueOf(jsonElement.getAsBoolean());
                }
            }
        });
        gsonBuilder.registerTypeAdapter(Boolean.class, new JsonDeserializer<Boolean>() {
            @Override
            public Boolean deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return Boolean.valueOf(jsonElement.getAsInt() != 0);
                } catch (NumberFormatException e) {
                    return Boolean.valueOf(jsonElement.getAsBoolean());
                }
            }
        });
        gsonBuilder.registerTypeAdapter(byte[].class, new JsonSerializer<byte[]>() {
            @Override
            public JsonElement serialize(byte[] data, Type type, JsonSerializationContext context) {
                return new JsonPrimitive(Base64.encodeToString(data, 2));
            }
        });
        gsonBuilder.registerTypeAdapter(byte[].class, new JsonDeserializer<byte[]>() {
            @Override
            public byte[] deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                try {
                    return Base64.decode(jsonElement.getAsString(), 2);
                } catch (IllegalStateException e) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    byte[] data = new byte[jsonArray.size()];
                    for (int i = 0; i < jsonArray.size(); i++) {
                        data[i] = jsonArray.get(i).getAsByte();
                    }
                    return data;
                }
            }
        });
        gsonBuilder.registerTypeAdapter(Integer.TYPE, new JsonDeserializer<Integer>() {
            @Override
            public Integer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
                return Integer.valueOf(jsonElement.getAsInt());
            }
        });
        try {
            Object factory = Class.forName("kotlinx.gson.KotlinJsonTypeAdapterFactory").newInstance();
            if (factory instanceof TypeAdapterFactory) {
                gsonBuilder.registerTypeAdapterFactory((TypeAdapterFactory) factory);
            }
        } catch (Exception ignored) {
        }
        return gsonBuilder;
    }

    public static String toJSON(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            LogUtil.e(TAG, "toJSON failed with obj=" + object, e);
            return null;
        }
    }

    public static <T> T fromJSON(String json, Class<T> classType) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, classType);
        } catch (Exception e) {
            LogUtil.e(TAG, "fromJSON failed with jsonStr=" + json + ", classType=" + classType, e);
            return null;
        }
    }

    /** Deserialises a parameterised type, e.g. {@code List.class, Foo.class}. */
    public static <T> T fromJSON(String json, Class<?> rawType, Class<?>... typeArguments) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        try {
            return GSON.fromJson(json, TypeToken.getParameterized(rawType, typeArguments).getType());
        } catch (Exception e) {
            LogUtil.e(TAG, "toCollection failed with jsonStr=" + json + ", collectionClass=" + rawType, e);
            return null;
        }
    }

    public static Object getJSONValue(String json, String key) {
        try {
            return new JSONObject(json).get(key);
        } catch (JSONException e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }
}
