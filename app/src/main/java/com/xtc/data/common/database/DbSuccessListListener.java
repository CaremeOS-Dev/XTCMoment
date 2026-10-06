package com.xtc.data.common.database;

import java.util.List;

/** Reports a successful database operation returning a list. */
public interface DbSuccessListListener<T> {

    void onSuccess(List<T> list);
}