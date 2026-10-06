package com.xtc.utils.encode;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/** MD5 helper returning the usual upper-case hex digest. */
public class MD5Util {

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static final String md5(byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(data);
            byte[] digest = messageDigest.digest();
            char[] result = new char[digest.length * 2];
            int index = 0;
            for (byte b : digest) {
                int next = index + 1;
                result[index] = HEX_DIGITS[(b >>> 4) & 15];
                index = next + 1;
                result[next] = HEX_DIGITS[b & 15];
            }
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final String md5(String text) {
        return md5(text.getBytes(Charset.forName("UTF-8")));
    }
}
