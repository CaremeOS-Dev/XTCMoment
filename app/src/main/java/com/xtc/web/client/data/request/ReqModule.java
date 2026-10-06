package com.xtc.web.client.data.request;

/** 查询模块开关的请求。 */
public class ReqModule {

    private boolean defaultValue;
    private int key;

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "ReqModule{key=" + this.key + ", defaultValue=" + this.defaultValue + '}';
    }
}