package com.google.webp;

/**
 * Native WebP encoder entry points. The implementation is provided by the
 * {@code webp} shared library packaged with the application.
 */
public final class libwebp {

    private libwebp() {
    }

    public static native byte[] encodeRGB(byte[] rgb, int width, int height, int stride, float quality);

    public static native byte[] encodeRGBA(byte[] rgba, int width, int height, int stride, float quality);

    public static native int getEncoderVersion();

    public static native int getDecoderVersion();
}