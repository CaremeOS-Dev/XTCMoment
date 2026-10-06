package com.xtc.moment.net.bean;

/** A moment as returned by the server after publishing. */
public class Moment {
    private String content;
    private long createTime;
    private int emotionId;
    private int id;
    private String location;
    private String lookupId;
    private String momentId;
    private String parentId;
    private Object resource;
    private int resourceId;
    private int type;
    private String watchId;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
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

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Object getResource() {
        return this.resource;
    }

    public void setResource(Object resource) {
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

    public String getLookupId() {
        return this.lookupId;
    }

    public void setLookupId(String lookupId) {
        this.lookupId = lookupId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getEmotionId() {
        return this.emotionId;
    }

    public void setEmotionId(int emotionId) {
        this.emotionId = emotionId;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Moment{id=" + this.id + ", momentId='" + this.momentId + "', watchId='" + this.watchId + "', type=" + this.type + ", content='" + this.content + "', resource=" + this.resource + ", resourceId=" + this.resourceId + ", createTime=" + this.createTime + ", lookupId='" + this.lookupId + "', parentId='" + this.parentId + "', emotionId=" + this.emotionId + ", location=" + this.location + '}';
    }
}
