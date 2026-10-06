package com.xtc.web.core.data.resp;

/** 拍照/选图结果，data 为图片路径。 */
public class RespPhoto {

    public interface Code {
        String CANCEL = "000002";
        String ENCODE_FAIL = "000004";
        String NOT_PERMISSION = "000003";
        String RETURN_FAIL = "000005";
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
        return "RespPhoto{code='" + this.code + "', data=" + this.data + '}';
    }
}