package com.xtc.web.core.data.req;

/** H5 探测 native 是否支持某方法（name 为 namespace.method，type 为 all/async/sync）。 */
public class ReqSupportMethod {

    private String name;
    private String type;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ReqSupportMethod{type='" + this.type + "', name='" + this.name + "'}";
    }
}