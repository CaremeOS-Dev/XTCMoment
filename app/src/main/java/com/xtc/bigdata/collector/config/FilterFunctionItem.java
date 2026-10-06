package com.xtc.bigdata.collector.config;

/**
 * 过滤埋点函数项：描述某个功能点的采集/安全等级与可见性。
 */
public class FilterFunctionItem {

    private String c;
    private String f;
    private String i;
    private String p;
    private String s;
    private int v;

    public FilterFunctionItem() {
    }

    public FilterFunctionItem(String functionName) {
        this.f = functionName;
    }

    public FilterFunctionItem(String functionName, String packageName) {
        this.f = functionName;
        this.p = packageName;
    }

    public String getF() {
        return this.f;
    }

    public void setF(String functionName) {
        this.f = functionName;
    }

    public String getP() {
        return this.p;
    }

    public void setP(String packageName) {
        this.p = packageName;
    }

    public String getC() {
        return this.c;
    }

    public void setC(String dataCollectLevel) {
        this.c = dataCollectLevel;
    }

    public String getS() {
        return this.s;
    }

    public void setS(String dataSecurityLevel) {
        this.s = dataSecurityLevel;
    }

    public int getV() {
        return this.v;
    }

    public void setV(int visibility) {
        this.v = visibility;
    }

    public String getI() {
        return this.i;
    }

    public void setI(String innerModel) {
        this.i = innerModel;
    }

    @Override
    public String toString() {
        return "FilterFunctionItem{functionName='" + this.f + "', packageName='" + this.p + "', dataCollectLevel='" + this.c + "', dataSecurityLevel='" + this.s + "', visibility=" + this.v + ", innerModel='" + this.i + "'}";
    }
}