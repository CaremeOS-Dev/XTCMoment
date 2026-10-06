package com.xtc.ui.widget.util;

import android.content.res.TypedArray;

/** TypedArray 安全读取工具。 */
public class TypeArrayUtils {

    public static boolean optBoolean(TypedArray typedArray, int index, boolean defaultValue) {
        return typedArray == null ? defaultValue : typedArray.getBoolean(index, defaultValue);
    }
}