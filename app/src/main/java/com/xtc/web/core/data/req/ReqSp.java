package com.xtc.web.core.data.req;

/** H5 读写本地 SharedPreferences 的请求。 */
public class ReqSp {

    private String key;
    private String value;

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ReqSp{key='" + this.key + "', value='" + this.value + "'}";
    }
}