package com.xtc.moment.event;

/**
 * IM 动态消息的载荷：消息类型与内容。
 */
public class IMMomentMsgData {

    int type;
    String content;

    public IMMomentMsgData(int type, String content) {
        this.type = type;
        this.content = content;
    }

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
        return "IMMomentMsgData{type=" + this.type + ", content='" + this.content + "'}";
    }
}