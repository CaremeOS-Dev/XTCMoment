package com.xtc.web.core.data.req;

/** native 调用 JS 方法的请求体，callId 用于关联 JS 的返回值。 */
public class ReqNativeCallJs {

    private int callId;
    private Object[] data;
    private String method;

    public ReqNativeCallJs(String method, int callId, Object[] data) {
        this.callId = callId;
        this.method = method;
        this.data = data;
    }

    public Object[] getData() {
        return this.data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }

    public int getCallId() {
        return this.callId;
    }

    public void setCallId(int callId) {
        this.callId = callId;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ReqNativeCallJs{data='" + this.data + "', callId=" + this.callId + ", method='" + this.method + "'}";
    }
}