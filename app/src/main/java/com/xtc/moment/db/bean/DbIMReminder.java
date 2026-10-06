package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** A pending IM "warm reminder" for one moment, keyed by moment id. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_IM_REMINDER)
public class DbIMReminder {

    public static final String DONE = "done";
    public static final String IM_REMINDER_INSERT_TIME = "insertTime";
    public static final String IM_REMINDER_STATUS = "status";
    public static final String NOT_MATCH = "notMatch";
    public static final String UNSET = "unset";

    /** Same value as {@code ReminderHelper.ONE_WEEK}. */
    private static final long ONE_WEEK = 604800000L;

    @DatabaseField(columnName = IM_REMINDER_INSERT_TIME)
    private long insertTime;

    @DatabaseField(columnName = DbReminder.REMINDER_LABEL)
    private String label;

    @DatabaseField(columnName = "momentId", id = true)
    private String momentId;

    @DatabaseField(columnName = "status")
    private String status;

    public DbIMReminder() {
    }

    public DbIMReminder(String label, String momentId) {
        this.label = label;
        this.momentId = momentId;
        this.status = UNSET;
        this.insertTime = System.currentTimeMillis() + ONE_WEEK;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getInsertTime() {
        return this.insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "DbIMReminder{label='" + this.label + "', momentId='" + this.momentId + "', status=" + this.status + "', insertTime=" + this.insertTime + '}';
    }
}
