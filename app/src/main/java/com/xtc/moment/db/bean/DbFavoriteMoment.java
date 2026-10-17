package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/**
 * 一条本地收藏的动态。
 *
 * <p>动态被删除时 {@code moment} / {@code moment_comment} / {@code like_message_info} 三张表都会被
 * 物理删除，所以收藏必须自带快照：{@link #snapshotJson} 里保存收藏那一刻的动态、评论列表与点赞
 * 记录，保证动态被删掉之后收藏列表仍能展示「最后一次的点赞数和评论列表」。
 */
@DatabaseTable(tableName = Constants.TableName.FAVORITE_MOMENT)
public class DbFavoriteMoment {

    public static final String CREATE_TIME_FIELD_NAME = "createTime";
    public static final String MOMENT_ID_FIELD_NAME = "momentId";
    public static final String MOMENT_WATCH_ID_FIELD_NAME = "momentWatchId";

    @DatabaseField(generatedId = true)
    private Integer id;

    /** 被收藏动态的 id，一条动态只允许收藏一次。 */
    @DatabaseField(unique = true)
    private String momentId;

    /** 被收藏动态的作者 watchId，用于取消收藏与兜底查询。 */
    @DatabaseField
    private String momentWatchId;

    /** 收藏那一刻的动态快照（动态 + 评论 + 点赞记录），JSON 串。 */
    @DatabaseField
    private String snapshotJson;

    /** 动态的展示名冗余保存，联系人被删后仍能显示作者。 */
    @DatabaseField
    private String momentName;

    /** 收藏时间，列表按它倒序展示。 */
    @DatabaseField
    private Long createTime;

    /** 快照最近一次刷新的时间。 */
    @DatabaseField
    private Long updateTime;

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

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public String getSnapshotJson() {
        return this.snapshotJson;
    }

    public void setSnapshotJson(String snapshotJson) {
        this.snapshotJson = snapshotJson;
    }

    public String getMomentName() {
        return this.momentName;
    }

    public void setMomentName(String momentName) {
        this.momentName = momentName;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DbFavoriteMoment{id=" + this.id + ", momentId='" + this.momentId + "', momentWatchId='"
                + this.momentWatchId + "', momentName='" + this.momentName + "', createTime=" + this.createTime
                + ", updateTime=" + this.updateTime + '}';
    }
}
