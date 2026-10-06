package com.xtc.moment.db.bean;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.module.StringConstant;
import com.xtc.moment.net.bean.MomentLbs;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;

/** A moment (post) row, including its embedded LBS and paging metadata. */
@DatabaseTable(tableName = "moment")
public class DbMoment {

    public static final String CREATE_TIME_FILED_NAME = "createTime";
    public static final String ID_FILED_NAME = "id";
    public static final String MOMENT_ID_FILED_NAME = "momentId";
    public static final String TYPE_FILED_NAME = "type";
    public static final String WATCH_ID_FILED_NAME = "watchId";

    public static final int OFF = 1;
    public static final int ON = 0;

    @DatabaseField
    private boolean checked;

    /** Not persisted. */
    private List<DbMomentComment> comments;

    @DatabaseField
    private int commentsPageNum;

    @DatabaseField
    private int commentsPageSize;

    @DatabaseField
    private int commentsTotalCount;

    @DatabaseField
    private int commentsTotalPage;

    @DatabaseField
    private String content;

    @DatabaseField
    private Long createTime;

    @DatabaseField
    private String dataUrl;

    @DatabaseField
    private String description;

    @DatabaseField
    private int emotionId;

    @DatabaseField
    private boolean enableLike;

    @DatabaseField
    private String iconPath;

    @DatabaseField(id = true)
    private Integer id;

    @DatabaseField
    private boolean isPreviewed;

    /** Not persisted. */
    private boolean isReportChecked;

    @DatabaseField
    private boolean isSkiped;

    @DatabaseField
    private Integer likeTotal;

    @DatabaseField
    private String location;

    @DatabaseField
    private String momentBgPath;

    @DatabaseField(unique = true)
    private String momentId;

    /** Not persisted. */
    private MomentLbs momentLbs;

    @DatabaseField
    private String momentLbsStr;

    @DatabaseField
    private String name;

    @DatabaseField
    private int official;

    @DatabaseField(defaultValue = StringConstant.RABBIT_WATCHID)
    private int permissionType;

    @DatabaseField
    private String photoListContent;

    @DatabaseField
    private String publishContent;

    @DatabaseField
    private String reminderContent;

    @DatabaseField
    private String reminderUrl;

    @DatabaseField
    private String resource;

    @DatabaseField
    private Integer resourceId;

    /** Not persisted. */
    private int retryCount;

    @DatabaseField
    private int scaleType;

    @DatabaseField
    private int supportComment;

    @DatabaseField
    private int top;

    @DatabaseField
    private long topExpireTime;

    @DatabaseField
    private Integer type;

    /** Not persisted. */
    private String typeList;

    @DatabaseField
    private String videoContent;

    @DatabaseField
    private String watchId;

    /** Not persisted. */
    private boolean showLbsAnim = false;

    /** Not persisted. */
    private boolean lbsSwitch = false;

    public String getReminderContent() {
        return this.reminderContent;
    }

    public void setReminderContent(String reminderContent) {
        this.reminderContent = reminderContent;
    }

    public String getReminderUrl() {
        return this.reminderUrl;
    }

    public void setReminderUrl(String reminderUrl) {
        this.reminderUrl = reminderUrl;
    }

    public void setVideoContent(String videoContent) {
        this.videoContent = videoContent;
    }

    public String getVideoContent() {
        return this.videoContent;
    }

    public void setPhotoListContent(String photoListContent) {
        this.photoListContent = photoListContent;
    }

    public String getPhotoListContent() {
        return this.photoListContent;
    }

    public void setPublishContent(String publishContent) {
        this.publishContent = publishContent;
    }

    public String getPublishContent() {
        return this.publishContent;
    }

    public void setReportChecked(boolean reportChecked) {
        this.isReportChecked = reportChecked;
    }

    public boolean isReportChecked() {
        return this.isReportChecked;
    }

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

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getLikeTotal() {
        return this.likeTotal;
    }

    public void setLikeTotal(Integer likeTotal) {
        this.likeTotal = likeTotal;
    }

    public boolean isEnableLike() {
        return this.enableLike;
    }

    public void setEnableLike(boolean enableLike) {
        this.enableLike = enableLike;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconPath() {
        return this.iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getScaleType() {
        return this.scaleType;
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSupportComment() {
        return this.supportComment;
    }

    public void setSupportComment(int supportComment) {
        this.supportComment = supportComment;
    }

    public boolean isSkiped() {
        return this.isSkiped;
    }

    public void setSkiped(boolean skiped) {
        this.isSkiped = skiped;
    }

    public boolean isPreviewed() {
        return this.isPreviewed;
    }

    public String getDataUrl() {
        return this.dataUrl;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public void setPreviewed(boolean previewed) {
        this.isPreviewed = previewed;
    }

    public List<DbMomentComment> getComments() {
        return this.comments;
    }

    public void setComments(List<DbMomentComment> comments) {
        this.comments = comments;
    }

    public String getTypeList() {
        return this.typeList;
    }

    public void setTypeList(String typeList) {
        this.typeList = typeList;
    }

    public int getCommentsPageNum() {
        return this.commentsPageNum;
    }

    public void setCommentsPageNum(int commentsPageNum) {
        this.commentsPageNum = commentsPageNum;
    }

    public int getCommentsPageSize() {
        return this.commentsPageSize;
    }

    public void setCommentsPageSize(int commentsPageSize) {
        this.commentsPageSize = commentsPageSize;
    }

    public int getCommentsTotalPage() {
        return this.commentsTotalPage;
    }

    public void setCommentsTotalPage(int commentsTotalPage) {
        this.commentsTotalPage = commentsTotalPage;
    }

    public int getCommentsTotalCount() {
        return this.commentsTotalCount;
    }

    public void setCommentsTotalCount(int commentsTotalCount) {
        this.commentsTotalCount = commentsTotalCount;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTop() {
        return this.top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public long getTopExpireTime() {
        return this.topExpireTime;
    }

    public void setTopExpireTime(long topExpireTime) {
        this.topExpireTime = topExpireTime;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public int getOfficial() {
        return this.official;
    }

    public void setOfficial(int official) {
        this.official = official;
    }

    public int getEmotionId() {
        return this.emotionId;
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = emotionId;
    }

    public String getMomentBgPath() {
        return this.momentBgPath;
    }

    public void setMomentBgPath(String momentBgPath) {
        this.momentBgPath = momentBgPath;
    }

    public void setPermissionType(int permissionType) {
        this.permissionType = permissionType;
    }

    public int getPermissionType() {
        return this.permissionType;
    }

    public String getMomentLbsStr() {
        MomentLbs lbs = this.momentLbs;
        if (lbs != null) {
            return JSONUtil.toJSON(lbs);
        }
        return this.momentLbsStr;
    }

    public void setMomentLbsStr(String momentLbsStr) {
        this.momentLbsStr = momentLbsStr;
    }

    public MomentLbs getMomentLbs() {
        String str = this.momentLbsStr;
        if (str != null) {
            return JSONUtil.fromJSON(str, MomentLbs.class);
        }
        return this.momentLbs;
    }

    public void parseMomentLbs() {
        this.momentLbs = JSONUtil.fromJSON(this.momentLbsStr, MomentLbs.class);
    }

    public void setMomentLbs(MomentLbs momentLbs) {
        this.momentLbs = momentLbs;
        this.momentLbsStr = JSONUtil.toJSON(momentLbs);
    }

    public boolean isShowLbsAnim() {
        MomentLbs lbs = getMomentLbs();
        return lbs != null && this.showLbsAnim && !TextUtils.isEmpty(this.location) && lbs.getStar() > 2;
    }

    public void setShowLbsAnim(boolean showLbsAnim) {
        this.showLbsAnim = showLbsAnim;
    }

    public boolean isLbsSwitch() {
        return this.lbsSwitch;
    }

    public void setLbsSwitch(boolean lbsSwitch) {
        this.lbsSwitch = lbsSwitch;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(500);
        sb.append("DbMoment{");
        sb.append("  momentId='");
        sb.append(this.momentId);
        sb.append('\'');
        sb.append(", watchId='");
        sb.append(this.watchId);
        sb.append('\'');
        sb.append(", type=");
        sb.append(this.type);
        sb.append(", official=");
        sb.append(this.official);
        sb.append(", resource='");
        sb.append(this.resource);
        sb.append('\'');
        sb.append(", createTime=");
        sb.append(this.createTime);
        sb.append(", likeTotal=");
        sb.append(this.likeTotal);
        sb.append(", enableLike=");
        sb.append(this.enableLike);
        sb.append(", checked=");
        sb.append(this.checked);
        sb.append(", name='");
        sb.append(this.name);
        sb.append('\'');
        sb.append(", iconPath='");
        sb.append(this.iconPath);
        sb.append('\'');
        sb.append(", photoListContent='");
        sb.append(this.photoListContent);
        sb.append('\'');
        sb.append(", commentsTotalCount='");
        sb.append(this.commentsTotalCount);
        sb.append('\'');
        sb.append(", dataUrl='");
        sb.append(this.dataUrl);
        sb.append('\'');
        sb.append(", comments=");
        sb.append(this.comments);
        sb.append(", content='");
        sb.append(this.content);
        sb.append('\'');
        sb.append(", emotionId='");
        sb.append(this.emotionId);
        sb.append('\'');
        sb.append(", momentBgPath='");
        sb.append(this.momentBgPath);
        sb.append('\'');
        sb.append(", location='");
        sb.append(this.location);
        sb.append('\'');
        sb.append(", permissionType='");
        sb.append(this.permissionType);
        sb.append('\'');
        sb.append(", momentLbs='");
        sb.append(this.momentLbsStr);
        sb.append('\'');
        sb.append(", reminderContent='");
        sb.append(this.reminderContent);
        sb.append('\'');
        sb.append(", reminderUrl='");
        sb.append(this.reminderUrl);
        sb.append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof DbMoment) {
            String momentId = this.momentId;
            return momentId != null && momentId.equals(((DbMoment) obj).getMomentId());
        }
        return super.equals(obj);
    }
}
