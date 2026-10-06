package com.xtc.web.core.data.resp;

/** 本地图片读取结果，data 为 base64 图片内容。 */
public class RespImage {

    public interface Code {
        String NOT_EXIST = "000002";
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
        return "RespImage{code='" + this.code + "', data='" + this.data + "'}";
    }
}