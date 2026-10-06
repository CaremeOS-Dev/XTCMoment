package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** One "like" notification for a moment. */
@DatabaseTable(tableName = Constants.TableName.LIKE_MESSAGE_INFO)
public class DbLikeMessage {

    public static final String CHECKED_FIELD_NAME = "checked";
    public static final String MOMENTWATCHID_FIELD_NAME = "momentWatchId";
    public static final String MOMENT_ID_FILED_NAME = "momentId";
    public static final String WATCHID_FIELD_NAME = "watchId";

    @DatabaseField(columnName = "checked")
    private boolean checked;

    @DatabaseField
    private Long createTime;

    @DatabaseField
    private int emotionId;

    @DatabaseField(generatedId = true)
    private Integer id;

    /** Not persisted. */
    private String likedPic;

    @DatabaseField
    private String momentId;

    @DatabaseField(columnName = "momentWatchId")
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

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
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

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getEmotionId() {
        return this.emotionId;
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = emotionId;
    }

    public String getLikedPic() {
        return this.likedPic;
    }

    public void setLikedPic(String likedPic) {
        this.likedPic = likedPic;
    }

    @Override
    public String toString() {
        return "DbLikeMessage{id=" + this.id + ", momentWatchId='" + this.momentWatchId + "', watchId='" + this.watchId + "', watchName='" + this.watchName + "', momentId='" + this.momentId + "', createTime=" + this.createTime + ", checked=" + this.checked + ", emotionId=" + this.emotionId + ", likedPic=" + this.likedPic + '}';
    }
}
