package com.xtc.web.core.data.resp;

/** 应用展示状态结果。 */
public class RespAppState {

    public interface Code {
        int CARRIAGE = 1;
        int UNDERCARRIAGE = 2;
    }

    private int code;
    private AppInfoResponse data;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public AppInfoResponse getData() {
        return this.data;
    }

    public void setData(AppInfoResponse data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespAppState{code=" + this.code + ", data=" + this.data + '}';
    }
}