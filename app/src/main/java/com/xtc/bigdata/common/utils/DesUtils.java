package com.xtc.bigdata.common.utils;

import android.text.TextUtils;

import com.bumptech.glide.load.Key;
import com.xtc.log.LogUtil;

import java.io.UnsupportedEncodingException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES 加解密工具（十六进制字符串表示）。
 */
public class DesUtils {

    private static final String TAG = "DesUtils";
    private static final String DEFAULT_KEY = "123456";

    private Cipher decryptCipher;
    private Cipher encryptCipher;

    public DesUtils() {
        this(DEFAULT_KEY);
    }

    public String encrypt(String text) throws Exception {
        return byteArrayToHexString(encrypt(text.getBytes(Key.CHARSET)));
    }

    public String decrypt(String text) throws Exception {
        return new String(decrypt(hexStringToByteArray(text)), Key.CHARSET);
    }

    private static String byteArrayToHexString(byte[] bytes) {
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

    private static byte[] hexStringToByteArray(String hex) {
        if (TextUtils.isEmpty(hex)) {
            return new byte[0];
        }
        byte[] rawBytes = null;
        try {
            rawBytes = hex.getBytes(Key.CHARSET);
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(TAG, e);
        }
        if (rawBytes == null) {
            return new byte[0];
        }
        byte[] result = new byte[rawBytes.length / 2];
        for (int i = 0; i < rawBytes.length; i += 2) {
            try {
                result[i / 2] = (byte) Integer.parseInt(new String(rawBytes, i, 2, Key.CHARSET), 16);
            } catch (UnsupportedEncodingException e) {
                LogUtil.e(TAG, e);
            }
        }
        return result;
    }

    private DesUtils(String key) {
        this.encryptCipher = null;
        this.decryptCipher = null;
        try {
            java.security.Key secretKey = getKey(key.getBytes(Key.CHARSET));
            this.encryptCipher = Cipher.getInstance("DES");
            this.encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);
            this.decryptCipher = Cipher.getInstance("DES");
            this.decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    private byte[] encrypt(byte[] data) throws Exception {
        return this.encryptCipher.doFinal(data);
    }

    private byte[] decrypt(byte[] data) throws Exception {
        return this.decryptCipher.doFinal(data);
    }

    private java.security.Key getKey(byte[] keyBytes) {
        byte[] paddedKey = new byte[8];
        for (int i = 0; i < keyBytes.length && i < paddedKey.length; i++) {
            paddedKey[i] = keyBytes[i];
        }
        return new SecretKeySpec(paddedKey, "DES");
    }
}
