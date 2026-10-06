package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** A publish template (mood/state/etc.) with its local resource. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_TEMPLATE)
public class DbTemplate {

    @DatabaseField
    private String content;

    @DatabaseField
    private long createTime;

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private String resource;

    @DatabaseField
    private int resourceId;

    @DatabaseField
    private int subType;

    @DatabaseField
    private int type;

    @DatabaseField
    private long updateTime;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubType() {
        return this.subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
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

    public int getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "DbTemplate{id=" + this.id + ", type=" + this.type + ", subType=" + this.subType + ", content='" + this.content + "', resource='" + this.resource + "', resourceId=" + this.resourceId + ", createTime=" + this.createTime + ", updateTime=" + this.updateTime + '}';
    }
}
