package com.xtc.httplib.bean;

import android.text.TextUtils;

import com.xtc.httplib.okhttp.BaseInterceptor;

/** Carries the negotiated {@code Eebbk-Key} together with the local AES key. */
public class EncryptData {

    private String aesKey;
    private String eebbkKey;
    private int rsaEncryptType;

    public EncryptData(String eebbkKey, String aesKey, int rsaEncryptType) {
        this.eebbkKey = eebbkKey;
        this.aesKey = aesKey;
        this.rsaEncryptType = rsaEncryptType;
    }

    public String getEebbkKey() {
        return this.eebbkKey;
    }

    public void setEebbkKey(String eebbkKey) {
        this.eebbkKey = eebbkKey;
    }

    /** @return the AES key, falling back to the built-in key when unset. */
    public String getAesKey() {
        return TextUtils.isEmpty(this.aesKey) ? BaseInterceptor.ENCRYPT_KEY : this.aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public int getRsaEncryptType() {
        return this.rsaEncryptType;
    }

    public void setRsaEncryptType(int rsaEncryptType) {
        this.rsaEncryptType = rsaEncryptType;
    }
}