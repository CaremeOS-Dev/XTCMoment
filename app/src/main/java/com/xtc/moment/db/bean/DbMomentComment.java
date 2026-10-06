package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** A comment or reply on a moment. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_COMMENT)
public class DbMomentComment {

    public static final String CHECKED_FIELD_NAME = "checked";
    public static final String COMMENTID_FIELD_NAME = "commentId";
    public static final String CREATE_TIME_FIELD_NAME = "createTime";
    public static final String DELETED_FIELD_NAME = "deleted";
    public static final String MOMENTID_FIELD_NAME = "momentId";
    public static final String MOMENTWATCHID_FIELD_NAME = "momentWatchId";
    public static final String MOMENT_WATCHID_FIELD_NAME = "watchId";
    public static final String MOMENT_WATCH_NAME_FIELD_NAME = "watchName";
    public static final String REPLYID_FIELD_NAME = "replyId";

    public static final int MEDIA_TYPE_TEXT = 1;
    public static final int TYPE_COMMENT = 1;
    public static final int TYPE_REPLY = 2;

    @DatabaseField(columnName = "checked")
    private boolean checked;

    @DatabaseField
    private String comment;

    @DatabaseField(columnName = "commentId", unique = true)
    private String commentId;

    @DatabaseField
    private Long createTime;

    @DatabaseField
    private boolean deleted;

    /** Not persisted. */
    private int giftType;

    @DatabaseField(generatedId = true)
    private Integer id;

    /** Not persisted. */
    private boolean isComment = true;

    /** Not persisted. */
    private boolean isReportChecked;

    @DatabaseField
    private int mediaType;

    @DatabaseField(columnName = "momentId")
    private String momentId;

    @DatabaseField(columnName = "momentWatchId")
    private String momentWatchId;

    @DatabaseField
    private String parentWatchId;

    @DatabaseField
    private String replyCommentId;

    @DatabaseField
    private String replyId;

    @DatabaseField
    private String replyName;

    @DatabaseField
    private String resource;

    @DatabaseField
    private Integer resourceId;

    @DatabaseField
    private int type;

    /** Not persisted. */
    private String watchIcon;

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

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getCommentId() {
        return this.commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getReplyId() {
        return this.replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyName() {
        return this.replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
    }

    public String getReplyCommentId() {
        return this.replyCommentId;
    }

    public void setReplyCommentId(String replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public String getParentWatchId() {
        return this.parentWatchId;
    }

    public void setParentWatchId(String parentWatchId) {
        this.parentWatchId = parentWatchId;
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

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Integer getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
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

    public boolean isDeleted() {
        return this.deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isReportChecked() {
        return this.isReportChecked;
    }

    public void setReportChecked(boolean reportChecked) {
        this.isReportChecked = reportChecked;
    }

    public boolean isCommentFlag() {
        return this.isComment;
    }

    public void setCommentFlag(boolean commentFlag) {
        this.isComment = commentFlag;
    }

    public int getGiftType() {
        return this.giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public String getWatchIcon() {
        return this.watchIcon;
    }

    public void setWatchIcon(String watchIcon) {
        this.watchIcon = watchIcon;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("DbMomentComment{");
        sb.append("id=");
        sb.append(this.id);
        sb.append(", momentId='");
        sb.append(this.momentId);
        sb.append('\'');
        sb.append(", commentId='");
        sb.append(this.commentId);
        sb.append('\'');
        sb.append(", momentWatchId='");
        sb.append(this.momentWatchId);
        sb.append('\'');
        sb.append(", type=");
        sb.append(this.type);
        sb.append(", mediaType=");
        sb.append(this.mediaType);
        sb.append(", parentWatchId='");
        sb.append(this.parentWatchId);
        sb.append('\'');
        sb.append(", replyId='");
        sb.append(this.replyId);
        sb.append('\'');
        sb.append(", replyName='");
        sb.append(this.replyName);
        sb.append('\'');
        sb.append(", replyCommentId='");
        sb.append(this.replyCommentId);
        sb.append('\'');
        sb.append(", watchId='");
        sb.append(this.watchId);
        sb.append('\'');
        sb.append(", watchName='");
        sb.append(this.watchName);
        sb.append('\'');
        sb.append(", comment='");
        sb.append(this.comment);
        sb.append('\'');
        sb.append(", resource='");
        sb.append(this.resource);
        sb.append('\'');
        sb.append(", resourceId='");
        sb.append(this.resourceId);
        sb.append('\'');
        sb.append(", createTime='");
        sb.append(this.createTime);
        sb.append('\'');
        sb.append(", checked='");
        sb.append(this.checked);
        sb.append('\'');
        sb.append(", deleted='");
        sb.append(this.deleted);
        sb.append('\'');
        sb.append(", giftType='");
        sb.append(this.giftType);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DbMomentComment) {
            String momentId = this.momentId;
            if (momentId != null) {
                DbMomentComment other = (DbMomentComment) obj;
                String commentId = this.commentId;
                if (momentId.equals(other.getMomentId()) && commentId != null && commentId.equals(other.getCommentId())) {
                    return true;
                }
            }
            return false;
        }
        return super.equals(obj);
    }
}
