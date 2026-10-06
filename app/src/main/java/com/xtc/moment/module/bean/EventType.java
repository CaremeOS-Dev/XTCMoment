package com.xtc.moment.module.bean;

/** Event-bus payload identifying the event kind. */
public class EventType {

    public static final int CONTACT_UPDATE = 2;
    public static final int HIGH_RISK_DIALOG = 4;
    public static final int RECEIVE_IM_REMINDER = 5;
    public static final int SYNC_LAUNCHER_DATA = 3;

    int type;

    public EventType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}