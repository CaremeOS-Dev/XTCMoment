package com.xtc.web.client.data.response;

/** H5 原生网络请求的响应。 */
public class RespJsHttp {

    public interface Code {
        String NOT_SUPPORT = "000003";
        String OTHER_ERROR = "000002";
        String SUCCESS = "000001";
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
        return "RespJsHttp{code='" + this.code + "', desc='" + this.desc + "', data='" + this.data + "'}";
    }
}