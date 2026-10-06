package com.xtc.bigdata.collector.utils;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Coder {

    private static final String TAG = "MD5Coder";
    private static final String KEY_MD5 = "MD5";

    private int radix;

    public MD5Coder(int radix) {
        this.radix = 36;
        this.radix = radix;
    }

    public MD5Coder() {
        this.radix = 36;
    }

    public String encode(byte[] data) throws Exception {
        return new BigInteger(getMD5(data)).abs().toString(radix);
    }

    private byte[] getMD5(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(KEY_MD5);
            digest.update(data);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, e + "");
            return null;
        }
    }
}