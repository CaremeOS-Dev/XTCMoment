package com.xtc.data.common.database;

/** Reports a successful database operation returning a single value. */
public interface DbSuccessConListener<T> {

    void onSuccess(T value);
}