package com.xtc.utils.security;

import android.text.TextUtils;

import java.util.Objects;

/** JNI bridge to the bundled {@code libxtcSecurity.so} key helpers. */
public class XtcSecurity {

    private static final String TAG = "XtcSecurity";
    /** Prefix marking an already encrypted secret. */
    private static final String ENCRYPTED_PREFIX = "KeyEncrypt";

    static {
        System.loadLibrary("xtcSecurity");
    }

    public static native String getDefaultRsaKey();

    public static native String secretKeyDecrypt(String value);

    public static native String secretKeyEncrypt(String value);

    /** Encrypts the secret and prefixes it, unless it is already encrypted. */
    public static String encryptSecret(String value) {
        if (TextUtils.isEmpty(value) || value.startsWith(ENCRYPTED_PREFIX)) {
            return value;
        }
        String encrypted = secretKeyEncrypt(value);
        if (Objects.equals(encrypted, value)) {
            return value;
        }
        return ENCRYPTED_PREFIX + encrypted;
    }

    /** Removes the prefix and decrypts, returning the input when not encrypted. */
    public static String decryptSecret(String value) {
        return (!TextUtils.isEmpty(value) && value.startsWith(ENCRYPTED_PREFIX))
                ? secretKeyDecrypt(value.substring(ENCRYPTED_PREFIX.length())) : value;
    }
}