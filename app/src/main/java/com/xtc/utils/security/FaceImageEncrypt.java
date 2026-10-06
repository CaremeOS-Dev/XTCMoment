package com.xtc.utils.security;

/** JNI bridge to the bundled {@code libxtcSecurity.so} face-image codec. */
public class FaceImageEncrypt {

    private static final String TAG = "FaceImageEncrypt";

    static {
        System.loadLibrary("xtcSecurity");
    }

    public static native byte[] decrypt(byte[] data);

    public static native byte[] encrypt(byte[] data);
}