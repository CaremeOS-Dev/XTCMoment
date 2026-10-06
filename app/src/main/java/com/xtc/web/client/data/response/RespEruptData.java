package com.xtc.web.client.data.response;

/** H5 主动触发 native 事件的响应。 */
public class RespEruptData {

    private int type;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "RespEruptData{type=" + this.type + '}';
    }
}