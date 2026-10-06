package com.xtc.moment.third.bean;

/**
 * 推送内容类型数据。
 */
public class PushContentBean {

    public static final int UNKNOWN = 0;
    public static final int MOOD = 1;
    public static final int STATE = 2;
    public static final int TEXT = 3;
    public static final int LOCATION = 4;
    public static final int PHOTO = 5;
    public static final int VIDEO = 6;
    public static final int VOICE = 7;

    private int type;
    private String content;

    public PushContentBean() {
    }

    public PushContentBean(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return String.valueOf(this.type);
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
        return "PushContentBean{type=" + this.type + ", content='" + this.content + "'}";
    }
}