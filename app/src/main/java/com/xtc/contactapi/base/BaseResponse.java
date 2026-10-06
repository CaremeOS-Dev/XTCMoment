package com.xtc.contactapi.base;

/**
 * 联系人 API 响应基类，携带响应码、泛型响应体与错误描述。
 */
public class BaseResponse<T> {

    private int responseCode;
    private T response;
    private String errorDesc;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    @Override
    public String toString() {
        return "BaseResponse{responsCode=" + responseCode + ", response=" + response + ", errorDesc='" + errorDesc + "'}";
    }
}