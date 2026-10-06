package com.xtc.utils.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Small helpers for null-safe collection checks. */
public class CollectionUtil {

    /** Returns {@code true} when the list contains the given string. */
    public static boolean contains(String value, List<String> list) {
        if (list == null || value == null) {
            return false;
        }
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (value.equals(iterator.next())) {
                return true;
            }
        }
        return false;
    }

    /** Returns {@code true} when the collection is null or empty. */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
}