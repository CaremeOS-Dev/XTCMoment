package com.xtc.bigdata.common.utils;

import android.content.Context;
import android.net.Uri;

import com.xtc.bigdata.common.db.constant.Columns;

/** Builds the behaviour provider URIs. */
public class UriUtils {

    private UriUtils() {
    }

    /** Content URI of the behaviour provider of the given package. */
    public static Uri getContentUri(Context context) {
        return Uri.parse("content://" + context.getPackageName() + ".bigdata/" + Columns.DIR_PATH);
    }
}