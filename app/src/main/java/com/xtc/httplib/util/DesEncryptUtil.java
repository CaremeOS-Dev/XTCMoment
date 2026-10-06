package com.xtc.httplib.util;

import com.qiniu.android.common.Constants;
import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/** DES/ECB/PKCS5 helpers used to obfuscate small local values. */
public class DesEncryptUtil {

    private static final String TAG = LogTag.tag("DesEncryptUtil");
    private static final String ENC_KEY = "<Yb0AzXu";
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private DesEncryptUtil() {
    }

    public static String encrypt(String text) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENC_KEY.getBytes(Constants.UTF_8), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return bytesToHexString(cipher.doFinal(text.getBytes(Constants.UTF_8)));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public static String decrypt(String text) {
        try {
            SecretKeySpec key = new SecretKeySpec(ENC_KEY.getBytes(Constants.UTF_8), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(hexStringToByte(text)));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    public static byte[] hexStringToByte(String hex) {
        int length = hex.length() / 2;
        byte[] result = new byte[length];
        char[] chars = hex.toCharArray();
        for (int i = 0; i < length; i++) {
            int index = i * 2;
            result[i] = (byte) (toByte(chars[index + 1]) | (toByte(chars[index]) << 4));
        }
        return result;
    }

    private static byte toByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    public static final String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() < 2) {
                builder.append('0');
            }
            builder.append(hex.toUpperCase());
        }
        return builder.toString();
    }
}