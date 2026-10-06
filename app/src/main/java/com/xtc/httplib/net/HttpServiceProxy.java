package com.xtc.httplib.net;

import android.content.Context;

import com.xtc.httplib.HttpClient;
import com.xtc.httplib.HttpManager;

/** Base class of the retrofit service proxies. */
public abstract class HttpServiceProxy {
    protected Context context;
    protected HttpClient httpClient;

    public HttpServiceProxy(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.context = applicationContext == null ? context : applicationContext;
        this.httpClient = HttpManager.getInstance(context).getHttpClient();
    }
}