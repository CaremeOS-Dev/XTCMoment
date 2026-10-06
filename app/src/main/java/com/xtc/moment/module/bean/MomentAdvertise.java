package com.xtc.moment.module.bean;

import com.xtc.moment.db.bean.DbMomentComment;

import java.util.List;

/**
 * 官方广告动态数据模型。
 */
public class MomentAdvertise {

    public static final int ADVERTISE_TEXT = 0;
    public static final int ADVERTISE_PHOTO = 1;
    public static final int ADVERTISE_PHOTO_TEXT = 2;
    public static final int ADVERTISE_VIDEO = 3;
    public static final int ADVERTISE_PHOTO_H5 = 4;

    public static final int OFF = 0;
    public static final int ON = 1;

    private String advertId;
    private String publisherId;
    private String name;
    private String icon;
    private int type;
    private String content;
    private String resource;
    private long publishTime;
    private int scaleType;
    private int likeTotal;
    private int enableLike;
    private List<DbMomentComment> comments;
    private String dataUrl;
    private int top;
    private int official;
    private long topExpireTime;

    public String getAdvertId() {
        return this.advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    public String getPublisherId() {
        return this.publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public long getPublishTime() {
        return this.publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public int getScaleType() {
        return this.scaleType;
    }

    public void setScaleType(int scaleType) {
        this.scaleType = scaleType;
    }

    public int getLikeTotal() {
        return this.likeTotal;
    }

    public void setLikeTotal(int likeTotal) {
        this.likeTotal = likeTotal;
    }

    public int getEnableLike() {
        return this.enableLike;
    }

    public void setEnableLike(int enableLike) {
        this.enableLike = enableLike;
    }

    public List<DbMomentComment> getComments() {
        return this.comments;
    }

    public void setComments(List<DbMomentComment> comments) {
        this.comments = comments;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public String getDataUrl() {
        return this.dataUrl;
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

    public int getOfficial() {
        return this.official;
    }

    public void setOfficial(int official) {
        this.official = official;
    }

    @Override
    public String toString() {
        return "MomentAdvertise{advertId='" + this.advertId + "', publisherId='" + this.publisherId + "', name='"
                + this.name + "', icon='" + this.icon + "', type=" + this.type + ", content='" + this.content
                + "', resource='" + this.resource + "', publishTime=" + this.publishTime + ", scaleType="
                + this.scaleType + ", likeTotal=" + this.likeTotal + ", enableLike=" + this.enableLike + ", dataUrl='"
                + this.dataUrl + "', top=" + this.top + ", official=" + this.official + ", topExpireTime="
                + this.topExpireTime + ", comments=" + this.comments + '}';
    }
}