package com.xtc.web.core.data.resp;

import com.xtc.web.core.data.LocationInfo;

/** 定位查询结果。 */
public class RespLocationInfo {

    public interface Code {
        String FAIL = "000002";
        String NOT_PERMISSION = "000003";
        String SUCCESS = "000001";
    }

    private String code;
    private LocationInfo data;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocationInfo getData() {
        return this.data;
    }

    public void setData(LocationInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespLocationInfo{code='" + this.code + "', data=" + this.data + '}';
    }
}