package com.xtc.architecture.mvp;

import android.content.Context;

/** Base for the "serve" (service-object) layer; holds the app context. */
public class BaseServe<T> {

    protected Context mContext;

    public BaseServe(Context context) {
        this.mContext = context.getApplicationContext();
    }
}
