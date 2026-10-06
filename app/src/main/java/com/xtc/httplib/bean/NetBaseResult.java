package com.xtc.httplib.bean;

/** Generic response envelope returned by every endpoint. */
public class NetBaseResult<T> {
    public static final String SUCCESS = "000001";

    private String code;
    private T data;
    private String desc;
    private NetPushError netPushError;
    private String serverGreyCode;
    private String watchTips;

    public NetPushError getNetPushError() {
        return this.netPushError;
    }

    public void setNetPushError(NetPushError netPushError) {
        this.netPushError = netPushError;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getServerGreyCode() {
        return this.serverGreyCode;
    }

    public void setServerGreyCode(String serverGreyCode) {
        this.serverGreyCode = serverGreyCode;
    }

    public String getWatchTips() {
        return this.watchTips;
    }

    public void setWatchTips(String watchTips) {
        this.watchTips = watchTips;
    }

    @Override
    public String toString() {
        return "NetBaseResult{code=\'" + this.code + "\', desc=\'" + this.desc + "\', data=" + this.data
                + ", netPushError=" + this.netPushError + ", serverGreyCode=\'" + this.serverGreyCode
                + "\', watchTips=\'" + this.watchTips + "\'}";
    }
}