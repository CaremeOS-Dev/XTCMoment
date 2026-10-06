package com.xtc.utils.system;

/** Build-flavour constants for API level and screen density. */
public final class Flavor {

    private Flavor() {
    }

    /** API level flavours. */
    public static final class Api {
        public static final String API_19 = "api19";
        public static final String API_25 = "api25";
    }

    /** Screen-density flavours. */
    public static final class Dpi {
        public static final String HDPI = "hdpi";
        public static final String XHDPI = "xhdpi";
    }

    /** @return true for the API 25 flavour. */
    public static boolean isApi25(String flavor) {
        return Api.API_25.equals(flavor);
    }

    /** @return true for the API 19 flavour. */
    public static boolean isApi19(String flavor) {
        return Api.API_19.equals(flavor);
    }
}