package com.xtc.utils.encode;

import java.nio.charset.Charset;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/** AES/ECB/PKCS5Padding helpers with Base64 (or raw byte) output. */
public class AESUtil {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    /** Encrypts {@code plainText} with {@code key} and Base64-encodes the result. */
    public static String encryptToBase64(String plainText, String key) {
        try {
            return Base64Util.encode(encryptBytes(plainText, key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Encrypts raw bytes with {@code key} and Base64-encodes the result. */
    public static String encryptToBase64(byte[] data, String key) {
        try {
            return Base64Util.encode(encryptBytes(data, key));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Decrypts a Base64 payload into a UTF-8 string. */
    public static String decryptFromBase64(String base64Text, String key) {
        try {
            return new String(decryptBytes(Base64Util.decode(base64Text), key), UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Decrypts a Base64 payload into raw bytes. */
    public static byte[] decryptFromBase64ToBytes(String base64Text, String key) {
        try {
            return decryptBytes(Base64Util.decode(base64Text), key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Encrypts a UTF-8 string into raw cipher bytes. */
    private static byte[] encryptBytes(String plainText, String key) throws Exception {
        Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plainText.getBytes(UTF_8));
    }

    /** Encrypts raw bytes. */
    private static byte[] encryptBytes(byte[] data, String key) throws Exception {
        Cipher cipher = createCipher(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /** Decrypts raw cipher bytes. */
    private static byte[] decryptBytes(byte[] data, String key) throws Exception {
        Cipher cipher = createCipher(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /** Builds an AES cipher with a raw {@code key} byte array. */
    private static Cipher createCipher(int mode, String key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(Charset.forName("UTF-8")), ALGORITHM);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(mode, secretKey);
        return cipher;
    }
}