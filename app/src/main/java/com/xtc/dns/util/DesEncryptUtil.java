package com.xtc.dns.util;

import com.qiniu.android.common.Constants;
import com.xtc.dns.LogTag;
import com.xtc.log.LogUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * DES 加解密与十六进制转换工具。
 */
public class DesEncryptUtil {

    private static final String TAG = LogTag.tag("DesEncryptUtil");
    private static final String DES_KEY = "<Yb0AzXu";
    private static final String HEX_CHARS = "0123456789abcdef";

    /** DES 加密并转为十六进制字符串。 */
    public static String encrypt(String plainText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(DES_KEY.getBytes(Constants.UTF_8), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return toHexString(cipher.doFinal(plainText.getBytes(Constants.UTF_8)));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    /** 十六进制字符串解密为明文。 */
    public static String decrypt(String hexText) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(DES_KEY.getBytes(Constants.UTF_8), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            return new String(cipher.doFinal(toByteArray(hexText)));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return null;
        }
    }

    /** 十六进制字符串转字节数组。 */
    public static byte[] toByteArray(String hexText) {
        int length = hexText.length() / 2;
        byte[] bytes = new byte[length];
        char[] chars = hexText.toCharArray();
        for (int i = 0; i < length; i++) {
            int index = i * 2;
            bytes[i] = (byte) (hexCharToByte(chars[index + 1]) | (hexCharToByte(chars[index]) << 4));
        }
        return bytes;
    }

    private static byte hexCharToByte(char c) {
        return (byte) HEX_CHARS.indexOf(c);
    }

    /** 字节数组转大写十六进制字符串。 */
    public static final String toHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer(bytes.length);
        for (byte b : bytes) {
            String hexString = Integer.toHexString(b & 0xFF);
            if (hexString.length() < 2) {
                buffer.append(0);
            }
            buffer.append(hexString.toUpperCase());
        }
        return buffer.toString();
    }
}