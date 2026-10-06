package com.xtc.web.core.data.resp;

import com.xtc.web.core.data.BatteryInfo;

/** 电量查询结果。 */
public class RespBatteryInfo {

    public interface Code {
        String FAIL = "000002";
        String SUCCESS = "000001";
    }

    private String code;
    private BatteryInfo data;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BatteryInfo getData() {
        return this.data;
    }

    public void setData(BatteryInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespBatteryInfo{code='" + this.code + "', data=" + this.data + '}';
    }
}