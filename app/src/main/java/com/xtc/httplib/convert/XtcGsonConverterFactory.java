package com.xtc.httplib.convert;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/** Retrofit converter factory backed by Gson. */
public final class XtcGsonConverterFactory extends Converter.Factory {

    private final Gson gson;

    public static XtcGsonConverterFactory create() {
        return create(new Gson());
    }

    public static XtcGsonConverterFactory create(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        return new XtcGsonConverterFactory(gson);
    }

    private XtcGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new XtcGsonResponseBodyConverter(this.gson, this.gson.getAdapter(TypeToken.get(type)));
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        return new XtcGsonRequestBodyConverter(this.gson, this.gson.getAdapter(TypeToken.get(type)));
    }
}