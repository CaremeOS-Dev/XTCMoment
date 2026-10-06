package com.xtc.moment.module.prerogative.bean;

/**
 * 特权资源下载信息。
 */
public class ResourceNetResponse {

    public static final int FUNCTION_TYPE_WEICAHT = 1;
    public static final int FUNCTION_TYPE_MOMENT_LIKE = 2;
    public static final int FUNCTION_TYPE_MOMENT_BACKGROUND = 3;

    private String dynamicName;
    private int id;
    private int functionType;
    private String dynamicUrl;
    private String dynamicVersion;

    public void setDynamicName(String dynamicName) {
        this.dynamicName = dynamicName;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }

    public void setDynamicUrl(String dynamicUrl) {
        this.dynamicUrl = dynamicUrl;
    }

    public void setDynamicVersion(String dynamicVersion) {
        this.dynamicVersion = dynamicVersion;
    }

    public String getDynamicName() {
        return this.dynamicName;
    }

    public int getId() {
        return this.id;
    }

    public int getFunctionType() {
        return this.functionType;
    }

    public String getDynamicUrl() {
        return this.dynamicUrl;
    }

    public String getDynamicVersion() {
        return this.dynamicVersion;
    }

    @Override
    public String toString() {
        return "ResourceNetResponse{dynamicName='" + this.dynamicName + "', id=" + this.id + ", functionType="
                + this.functionType + ", dynamicUrl='" + this.dynamicUrl + "', dynamicVersion='" + this.dynamicVersion
                + "'}";
    }
}