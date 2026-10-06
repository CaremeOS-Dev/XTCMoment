package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** Rate-limit record for gifts sent to a moment. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_GIFT_RECORD)
public class DbGiftRecord {

    public static final String MOMENT_ID_FIELD_NAME = "momentId";
    public static final int SEND_GIFT_COUNT = 5;
    public static final int TEM_MINUTE = 10;

    @DatabaseField
    private int count;

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private long lastGiftTime;

    @DatabaseField(columnName = "momentId")
    private String momentId;

    @DatabaseField
    private String momentWatchId;

    @DatabaseField
    private String watchId;

    @DatabaseField
    private String watchName;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWatchName() {
        return this.watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public long getLastGiftTime() {
        return this.lastGiftTime;
    }

    public void setLastGiftTime(long lastGiftTime) {
        this.lastGiftTime = lastGiftTime;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static DbGiftRecord buildGift(String momentId, String momentWatchId, String watchId, String watchName) {
        DbGiftRecord record = new DbGiftRecord();
        record.setMomentId(momentId);
        record.setMomentWatchId(momentWatchId);
        record.setWatchName(watchName);
        record.setWatchId(watchId);
        record.setLastGiftTime(System.currentTimeMillis());
        record.setCount(1);
        return record;
    }

    @Override
    public String toString() {
        return "DbGiftRecord{watchName='" + this.watchName + "', momentId='" + this.momentId + "', watchId='" + this.watchId + "', momentWatchId='" + this.momentWatchId + "', lastGiftTime=" + this.lastGiftTime + ", count=" + this.count + '}';
    }
}
