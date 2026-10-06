package com.xtc.utils.common;

import java.util.Arrays;
import java.util.Comparator;

/** Backport of the useful parts of {@link java.util.Objects} for API 19. */
public final class Objects {

    private Objects() {
        throw new AssertionError("No java.util.Objects instances for you!");
    }

    public static boolean equals(Object first, Object second) {
        return first == second || (first != null && first.equals(second));
    }

    public static int hashCode(Object object) {
        if (object != null) {
            return object.hashCode();
        }
        return 0;
    }

    public static int hash(Object... values) {
        return Arrays.hashCode(values);
    }

    public static String toString(Object object) {
        return String.valueOf(object);
    }

    public static String toString(Object object, String nullDefault) {
        return object != null ? object.toString() : nullDefault;
    }

    public static <T> int compare(T first, T second, Comparator<? super T> comparator) {
        if (first == second) {
            return 0;
        }
        return comparator.compare(first, second);
    }

    public static <T> T requireNonNull(T object) {
        if (object != null) {
            return object;
        }
        throw new NullPointerException();
    }

    public static <T> T requireNonNull(T object, String message) {
        if (object != null) {
            return object;
        }
        throw new NullPointerException(message);
    }
}