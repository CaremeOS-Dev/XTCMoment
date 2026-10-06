package com.xtc.moment.serve.bean;

import com.xtc.moment.net.bean.MomentLbs;

/**
 * 动态消息推送载荷。
 */
public class MomentMessageData {

    private String watchId;
    private String watchName;
    private String content;
    private String resource;
    private String momentId;
    private int type;
    private int resourceId;
    private long createTime;
    private String typeList;
    private int emotionId;
    private String location;
    private MomentLbs momentLbs;

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

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getTypeList() {
        return this.typeList;
    }

    public void setTypeList(String typeList) {
        this.typeList = typeList;
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

    public MomentLbs getMomentLbs() {
        return this.momentLbs;
    }

    public void setMomentLbs(MomentLbs momentLbs) {
        this.momentLbs = momentLbs;
    }

    @Override
    public String toString() {
        return "MomentMessageData{watchId='" + this.watchId + "', watchName='" + this.watchName + "', content='"
                + this.content + "', resource='" + this.resource + "', momentId='" + this.momentId + "', type="
                + this.type + ", resourceId=" + this.resourceId + ", createTime=" + this.createTime + ", typeList="
                + this.typeList + ", emotionId=" + this.emotionId + ", location='" + this.location + "', momentLbs="
                + this.momentLbs + '}';
    }
}