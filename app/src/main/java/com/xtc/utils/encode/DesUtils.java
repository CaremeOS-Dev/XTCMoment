package com.xtc.utils.encode;

import android.text.TextUtils;

import java.nio.charset.Charset;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/** DES/ECB helpers with hex output, defaulting to the legacy key "123456". */
public class DesUtils {

    private static final String DEFAULT_KEY = "123456";
    private static final String ALGORITHM = "DES";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private Cipher encryptCipher;
    private Cipher decryptCipher;

    public DesUtils() {
        this(DEFAULT_KEY);
    }

    /** Encrypts {@code text} and returns the lower-case hex representation. */
    public String encryptToHex(String text) throws Exception {
        return toHex(encrypt(text.getBytes(UTF_8)));
    }

    /** Decrypts a hex string back into a UTF-8 string. */
    public String decryptFromHex(String hexText) throws Exception {
        return new String(decrypt(fromHex(hexText)), UTF_8);
    }

    /** Formats the bytes as lower-case hex with two digits per byte. */
    private static String toHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (int value : bytes) {
            while (value < 0) {
                value += 256;
            }
            if (value < 16) {
                builder.append("0");
            }
            builder.append(Integer.toString(value, 16));
        }
        return builder.toString();
    }

    /** Parses a hex string into bytes, returning an empty array when invalid. */
    private static byte[] fromHex(String hexText) {
        if (TextUtils.isEmpty(hexText)) {
            return new byte[0];
        }
        byte[] raw = null;
        try {
            raw = hexText.getBytes(UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (raw == null) {
            return new byte[0];
        }
        int length = raw.length;
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            try {
                result[i / 2] = (byte) Integer.parseInt(new String(raw, i, 2, UTF_8), 16);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private DesUtils(String key) {
        this.encryptCipher = null;
        this.decryptCipher = null;
        try {
            Key secretKey = buildKey(key.getBytes(UTF_8));
            this.encryptCipher = Cipher.getInstance(ALGORITHM);
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            this.decryptCipher = Cipher.getInstance(ALGORITHM);
            this.decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] encrypt(byte[] data) throws Exception {
        return this.encryptCipher.doFinal(data);
    }

    private byte[] decrypt(byte[] data) throws Exception {
        return this.decryptCipher.doFinal(data);
    }

    /** Builds an 8-byte DES key by padding/truncating the raw key bytes. */
    private Key buildKey(byte[] rawKey) {
        byte[] keyBytes = new byte[8];
        for (int i = 0; i < rawKey.length && i < keyBytes.length; i++) {
            keyBytes[i] = rawKey[i];
        }
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}