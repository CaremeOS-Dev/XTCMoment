package com.xtc.utils.encode;

import java.util.UUID;

/** Generates a compact 32-char UUID string with the dashes removed. */
public class UUIDUtil {

    public static String randomUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23) + uuid.substring(24);
    }
}
