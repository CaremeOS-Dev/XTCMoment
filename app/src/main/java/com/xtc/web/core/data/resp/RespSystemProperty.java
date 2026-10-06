package com.xtc.web.core.data.resp;

/** 系统属性读取结果，data 为属性的字符串形式。 */
public class RespSystemProperty {

    public interface Code {
        String ARG_ERROR = "000002";
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
        return "RespAppInfo{code='" + this.code + "', data=" + this.data + '}';
    }
}