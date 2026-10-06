package com.xtc.moment.common.base;

/**
 * 参数校验工具。
 */
public class Preconditions {

    public static <T> T checkNotNull(T reference) {
        if (reference != null) {
            return reference;
        }
        throw new NullPointerException();
    }
}