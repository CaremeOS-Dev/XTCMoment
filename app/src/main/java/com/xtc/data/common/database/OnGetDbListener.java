package com.xtc.data.common.database;

/** Callback of an operation that returns a single value. */
public abstract class OnGetDbListener<T> implements DbFailListener, DbSuccessConListener<T> {
}