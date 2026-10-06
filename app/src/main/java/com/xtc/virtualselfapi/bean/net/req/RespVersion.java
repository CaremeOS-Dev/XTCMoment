package com.xtc.virtualselfapi.bean.net.req;

/**
 * 资源版本响应。
 */
public class RespVersion {

    private String scale;
    private int versionNumber;

    public String getScale() {
        return this.scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public String toString() {
        return "RespVersion{versionNumber=" + this.versionNumber + ", scale='" + this.scale + "'}";
    }
}