package com.xtc.bigdata.collector.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/** MD5 encoder truncating the digest to {@code length} characters. */
public class MD5Coder {

    private final int length;

    public MD5Coder(int length) {
        this.length = length;
    }

    /** Returns the lower-case hex digest truncated to the configured length. */
    public String encode(byte[] data) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(data);
        byte[] digest = messageDigest.digest();
        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            int value = b & 0xFF;
            if (value < 16) {
                builder.append('0');
            }
            builder.append(Integer.toHexString(value));
        }
        String hex = builder.toString();
        return hex.length() > this.length ? hex.substring(0, this.length) : hex;
    }

    public String encode(String text) throws Exception {
        return encode(text.getBytes(Charset.forName("UTF-8")));
    }
}