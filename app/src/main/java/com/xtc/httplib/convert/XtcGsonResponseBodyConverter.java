package com.xtc.httplib.convert;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.xtc.httplib.auth.HttpAuthManager;
import com.xtc.httplib.bean.NetBaseResult;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/** Deserialises a response body and validates the auth code. */
final class XtcGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private final TypeAdapter<T> adapter;
    private final Gson gson;

    XtcGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        JsonReader jsonReader = this.gson.newJsonReader(responseBody.charStream());
        try {
            T result = this.adapter.read(jsonReader);
            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonIOException("JSON document was not fully consumed.");
            }
            if (result instanceof NetBaseResult) {
                HttpAuthManager.getInstance().checkAuth(((NetBaseResult) result).getCode(), 1);
            }
            responseBody.close();
            return result;
        } catch (Throwable t) {
            responseBody.close();
            throw t;
        }
    }
}