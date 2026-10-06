package com.xtc.web.core.data.req;

/** 读取系统属性请求，type 决定按字符串/整型/长整型/布尔解析。 */
public class ReqSystemProperty {

    private String propertyName;
    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {
        return "ReqSystemProperty{type=" + this.type + ", propertyName='" + this.propertyName + "'}";
    }
}