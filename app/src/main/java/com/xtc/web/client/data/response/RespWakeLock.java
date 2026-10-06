package com.xtc.web.client.data.response;

/** WakeLock 申请结果。 */
public class RespWakeLock {

    public interface Code {
        String FAIL = "000002";
        String SUCCESS = "000001";
    }

    private String code;
    private String data;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespWakeLock{code='" + this.code + "', data='" + this.data + "'}";
    }
}