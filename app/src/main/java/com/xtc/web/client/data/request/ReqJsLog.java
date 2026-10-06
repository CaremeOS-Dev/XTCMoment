package com.xtc.web.client.data.request;

/** H5 上报日志的请求。 */
public class ReqJsLog {

    private String msg;
    private String tag;
    private String type;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ReqJsLog{type='" + this.type + "', tag='" + this.tag + "', msg='" + this.msg + "'}";
    }
}