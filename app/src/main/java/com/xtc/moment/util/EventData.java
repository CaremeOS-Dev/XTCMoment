package com.xtc.moment.util;

/**
 * EventBus 事件载体：类型 + 数据。
 */
public class EventData<T> {

    public static final int BATCH_DELETE_MOMENT = 17;
    public static final int CANCEL_LIKE_CHANGE_MOMENTADAPTER = 11;
    public static final int CANCEL_LIKE_CHANGE_MOMENT_ADAPTER_MOMENT = 13;
    public static final int CANCEL_LIKE_CHANGE_MOMENT_ADAPTER_SHARE = 12;
    public static final int CHANGE_TO_REPORTED = 10;
    public static final int CHANGE_VISIBLE_MOMENT = 18;
    public static final int CHECK_MOMENT_REMINDER = 19;
    public static final int CHECK_SHARE_REMINDER = 21;
    public static final int CONTACT_DEL = 7;
    public static final int DELETE_COMMENT = 2;
    public static final int DELETE_INVALIDATE_MOMENT = 9;
    public static final int DELETE_MOMENT = 1;
    public static final int LIKE_MOMENT = 4;
    public static final int LOCAL_PUBLISH_MOMENT = 5;
    public static final int PLAY_THE_FULL_VIDEO = 16;
    public static final int PUBLISH_COMMENT = 3;
    public static final int PUBLISH_MOMENT = 6;
    public static final int REFRESH_MOMENT_COMMENTS = 14;
    public static final int REFRESH_MOMENT_NEW_MESSAGE_VIEW = 15;
    public static final int SHOW_MOMENT_REMINDER = 20;
    public static final int SHOW_SHARE_REMINDER = 22;
    public static final int UPDATE_PIC_LOCAL_PATH = 8;

    private T data;
    private int type;

    public EventData(int type, T data) {
        this.type = type;
        this.data = data;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EventData{type=" + this.type + ", data=" + this.data + '}';
    }
}