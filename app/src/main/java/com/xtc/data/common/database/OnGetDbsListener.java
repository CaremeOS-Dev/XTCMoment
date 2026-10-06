package com.xtc.data.common.database;

/** Callback of an operation that returns a list. */
public abstract class OnGetDbsListener<T> implements DbFailListener, DbSuccessListListener<T> {
}