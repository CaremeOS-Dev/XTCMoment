package com.xtc.httplib.convert;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Converter;

/** Serialises a request body as UTF-8 JSON. */
final class XtcGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final TypeAdapter<T> adapter;
    private final Gson gson;

    XtcGsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        Buffer buffer = new Buffer();
        JsonWriter jsonWriter = this.gson.newJsonWriter(new OutputStreamWriter(buffer.outputStream(), UTF_8));
        this.adapter.write(jsonWriter, value);
        jsonWriter.close();
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }
}