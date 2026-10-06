package com.xtc.database.ormlite;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Small collection helpers of the database package. */
public class CollectionUtil {

    /** @return true when [list] contains [value]. */
    public static boolean contains(String value, List<String> list) {
        if (list != null && value != null) {
            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                if (value.equals(iterator.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** @return true when [collection] is null or empty. */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
}