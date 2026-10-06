package com.xtc.bigdata.collector.utils;

import java.util.Collection;

/** Collection helpers. */
public class CollectionUtil {

    private CollectionUtil() {
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
}