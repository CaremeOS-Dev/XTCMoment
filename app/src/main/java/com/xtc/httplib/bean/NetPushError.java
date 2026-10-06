package com.xtc.httplib.bean;

import java.util.List;

/** Server-side push error payload. */
public class NetPushError {
    private Integer code;
    private List error;
    private String identify;

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getIdentify() {
        return this.identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public List getError() {
        return this.error;
    }

    public void setError(List error) {
        this.error = error;
    }
}