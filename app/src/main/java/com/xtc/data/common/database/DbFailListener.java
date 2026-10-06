package com.xtc.data.common.database;

/** Reports a failed database operation. */
public interface DbFailListener {

    void onFail(Exception exception);
}