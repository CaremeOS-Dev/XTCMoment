package com.xtc.httplib.okhttp;

import android.content.Context;

import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.UUIDUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/** Base class of the okhttp interceptors. */
public abstract class BaseInterceptor implements Interceptor {

    protected Context context;

    private static final String TAG = LogTag.tag("BaseInterceptor");

    /** Default AES key used before the server key is available. */
    public static final String ENCRYPT_KEY = UUIDUtil.randomUUID().substring(0, 16);

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        return null;
    }

    public BaseInterceptor() {
    }

    BaseInterceptor(Context context) {
        if (context == null) {
            return;
        }
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext != null ? applicationContext : context;
    }

    /** Appends a uuid query parameter unless it is already present. */
    String getRedirectURL(String url, boolean hasUuid) {
        if (hasUuid) {
            return url;
        }
        if (url.contains("?")) {
            return url + "&uuid=" + UUIDUtil.randomUUID();
        }
        return url + "?uuid=" + UUIDUtil.randomUUID();
    }

    /** Removes the uuid query parameter from the url. */
    protected String subUrl(String url) {
        int index = url.indexOf("?uuid=");
        if (index == -1) {
            index = url.indexOf("&uuid=");
        }
        if (index != -1) {
            url = url.substring(0, index);
        }
        LogUtil.i(TAG, "http request sub url:" + url);
        return url;
    }
}