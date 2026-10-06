package com.xtc.bigdata.collector.config;

/**
 * 新版过滤埋点函数项：只包含采集等级与功能名。
 */
public class NewFilterFunctionItem {

    private String c;
    private String f;

    public NewFilterFunctionItem(String dataCollectLevel, String functionName) {
        this.c = dataCollectLevel;
        this.f = functionName;
    }

    public void setC(String dataCollectLevel) {
        this.c = dataCollectLevel;
    }

    public String getC() {
        return this.c;
    }

    public void setF(String functionName) {
        this.f = functionName;
    }

    public String getF() {
        return this.f;
    }

    @Override
    public String toString() {
        return "{\"NewFilterFunctionItem\":{\"c\":\"" + this.c + "\",\"f\":\"" + this.f + "\"}}";
    }
}