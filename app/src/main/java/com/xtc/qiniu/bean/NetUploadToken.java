package com.xtc.qiniu.bean;

/** Upload token returned by the server. */
public class NetUploadToken {

    private static final long serialVersionUID = 1;

    public Long timestamp;
    public String token;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}