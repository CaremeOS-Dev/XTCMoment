package com.xtc.assistantapi.message;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.xtc.assistantapi.LogTag;

import java.lang.reflect.Type;

/**
 * 指令反序列化器，按 header 的命名空间/指令名解析具体 payload 类型。
 */
public class DirectiveDeserializer implements JsonDeserializer<Directive> {

    private static final String TAG = LogTag.of("DirectiveDeserializer");

    private final Gson gson = new Gson();

    @Override
    public Directive deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null) {
            return new Directive();
        }
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject == null) {
            return new Directive();
        }
        String rawMessage = jsonObject.toString();
        JsonElement headerElement = jsonObject.get("header");
        Header header = gson.fromJson(headerElement, Header.class);
        Log.d(TAG, "headerElement = " + headerElement);
        Log.d(TAG, "header = " + header);
        JsonElement payloadElement = jsonObject.get("payload");
        Log.d(TAG, "payloadElement = " + payloadElement);
        return createDirective(header, payloadElement, rawMessage);
    }

    private Directive createDirective(Header header, JsonElement payloadElement, String rawMessage) {
        return new Directive(header, payloadElement, rawMessage);
    }
}