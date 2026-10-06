package com.xtc.database.ormlite;

/** Callback of an asynchronous dao operation. */
public interface DbCallBack {

    void onResult(Object result);
}