package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** Tracks a user's illegal-post count and sanction window. */
@DatabaseTable(tableName = Constants.TableName.ILLEGAL_RECORD)
public class DbIllegalRecord {

    public static final String WATCH_ID = "watchId";

    @DatabaseField
    private long endTime;

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private int illegalCount;

    @DatabaseField
    private boolean reportState;

    @DatabaseField
    private int sanctionState;

    @DatabaseField
    private long startTime;

    @DatabaseField
    private String watchId;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getIllegalCount() {
        return this.illegalCount;
    }

    public void setIllegalCount(int illegalCount) {
        this.illegalCount = illegalCount;
    }

    public int getSanctionState() {
        return this.sanctionState;
    }

    public void setSanctionState(int sanctionState) {
        this.sanctionState = sanctionState;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isReportState() {
        return this.reportState;
    }

    public void setReportState(boolean reportState) {
        this.reportState = reportState;
    }

    @Override
    public String toString() {
        return "DbIllegalRecord{id=" + this.id + ", watchId='" + this.watchId + "', illegalCount=" + this.illegalCount + ", sanctionState=" + this.sanctionState + ", startTime=" + this.startTime + ", endTime=" + this.endTime + ", reportState=" + this.reportState + '}';
    }
}
