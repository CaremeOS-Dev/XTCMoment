package com.xtc.system.account.bean;

import android.text.TextUtils;

/** HTTP configuration pushed by the init service. */
public class HttpConfig {
    private String ae;
    private String encSwitch;
    private String grey;
    private String httpHeadParam;
    private String rsaPublicKey;
    private String selfRsaPublicKey;
    private int ts;

    public HttpConfig(String grey, String encSwitch, String rsaPublicKey, String selfRsaPublicKey,
                      String httpHeadParam, int ts, String ae) {
        this.grey = grey;
        this.encSwitch = encSwitch;
        this.rsaPublicKey = rsaPublicKey;
        this.selfRsaPublicKey = selfRsaPublicKey;
        this.httpHeadParam = httpHeadParam;
        this.ts = ts;
        this.ae = ae;
    }

    public String getGrey() {
        return this.grey;
    }

    public void setGrey(String grey) {
        this.grey = grey;
    }

    public String getEncSwitch() {
        return this.encSwitch;
    }

    public void setEncSwitch(String encSwitch) {
        this.encSwitch = encSwitch;
    }

    public String getRsaPublicKey() {
        return this.rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getSelfRsaPublicKey() {
        return this.selfRsaPublicKey;
    }

    public void setSelfRsaPublicKey(String selfRsaPublicKey) {
        this.selfRsaPublicKey = selfRsaPublicKey;
    }

    public String getHttpHeadParam() {
        return this.httpHeadParam;
    }

    public void setHttpHeadParam(String httpHeadParam) {
        this.httpHeadParam = httpHeadParam;
    }

    public int getTs() {
        return this.ts;
    }

    public void setTs(int ts) {
        this.ts = ts;
    }

    public String getAe() {
        return this.ae;
    }

    public void setAe(String ae) {
        this.ae = ae;
    }

    @Override
    public String toString() {
        return "HttpConfig{grey=\'" + this.grey + "\', encSwitch=\'" + this.encSwitch + "\', rsaPublicKey=\'"
                + this.rsaPublicKey + "\', selfRsaPublicKey=\'" + this.selfRsaPublicKey + "\', httpHeadParam=\'"
                + this.httpHeadParam + "\', ts =\'" + this.ts + "\', ae null=\'" + TextUtils.isEmpty(this.ae) + "\'}";
    }
}