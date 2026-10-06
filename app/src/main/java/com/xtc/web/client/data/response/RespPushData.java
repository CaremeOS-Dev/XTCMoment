package com.xtc.web.client.data.response;

/** 推送给 H5 的数据。 */
public class RespPushData {

    private String content;
    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "RespPushData{type=" + this.type + ", content='" + this.content + "'}";
    }
}