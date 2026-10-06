package com.xtc.web.client.data.response;

/** 运动数据查询结果。 */
public class RespMotion {

    public interface Code {
        String FAIL = "000002";
        String SUCCESS = "000001";
    }

    private String code;
    private int data;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getData() {
        return this.data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespMotion{code='" + this.code + "', data=" + this.data + '}';
    }
}