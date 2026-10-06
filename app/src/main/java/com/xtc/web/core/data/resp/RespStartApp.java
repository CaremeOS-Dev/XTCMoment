package com.xtc.web.core.data.resp;

/** 打开第三方应用的结果。 */
public class RespStartApp {

    public interface Code {
        String FORBID = "4";
        String NOT_DISPLAY = "3";
        String NOT_INSTALL = "1";
        String NOT_UPDATE = "2";
        String SUCCESS = "0";
        String URI_ERROR = "6";
    }

    private String code;
    private String data;
    private String desc;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespStartApp{code='" + this.code + "', desc='" + this.desc + "', data='" + this.data + "'}";
    }
}