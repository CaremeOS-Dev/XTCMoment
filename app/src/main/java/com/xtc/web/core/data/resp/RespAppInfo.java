package com.xtc.web.core.data.resp;

import com.xtc.web.core.data.AppInfo;

/** 应用信息查询结果。 */
public class RespAppInfo {

    public interface Code {
        String NOT_EXIST = "000002";
        String SUCCESS = "000001";
    }

    private String code;
    private AppInfo data;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AppInfo getData() {
        return this.data;
    }

    public void setData(AppInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespAppInfo{code='" + this.code + "', data=" + this.data + '}';
    }
}