package com.xtc.utils.encode;

import java.util.Random;

/** Small helpers for generating random strings. */
public final class RandomUtils {

    private static final char[] LOWERCASE = "abcdefghijklmnopqrstuvwxyz".toCharArray();

    private RandomUtils() {
    }

    public static String randomLetters(Random random, int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(LOWERCASE[random.nextInt(26)]);
        }
        return builder.toString();
    }
}
