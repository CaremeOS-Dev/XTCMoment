package com.xtc.moment.net.bean;

import com.xtc.moment.db.bean.DbReminder;

import java.util.List;

/** Warm-reminder configuration pulled from the server. */
public class ReminderConfig {
    private List<DbReminder> WarmReminderList;
    private int warmTime;

    public List<DbReminder> getReminderList() {
        return this.WarmReminderList;
    }

    public void setReminderList(List<DbReminder> reminderList) {
        this.WarmReminderList = reminderList;
    }

    public int getWarmTime() {
        return this.warmTime;
    }

    public void setWarmTime(int warmTime) {
        this.warmTime = warmTime;
    }

    @Override
    public String toString() {
        return "ReminderConfig{reminderList=" + this.WarmReminderList + ", warmTime=" + this.warmTime + '}';
    }
}
