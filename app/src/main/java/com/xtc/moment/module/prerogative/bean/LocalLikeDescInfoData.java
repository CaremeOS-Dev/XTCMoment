package com.xtc.moment.module.prerogative.bean;

import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;

import java.util.List;

/**
 * 本地点赞特效资源描述。
 */
public class LocalLikeDescInfoData {

    private String name;
    private int count;
    private String descriptionText;
    private int version;
    private List<DbMomentPrerogativeLike> emotions;

    public void setName(String name) {
        this.name = name;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public int getCount() {
        return this.count;
    }

    public String getDescriptionText() {
        return this.descriptionText;
    }

    public int getVersion() {
        return this.version;
    }

    public List<DbMomentPrerogativeLike> getEmotions() {
        return this.emotions;
    }

    public void setEmotions(List<DbMomentPrerogativeLike> emotions) {
        this.emotions = emotions;
    }

    @Override
    public String toString() {
        return "LocalDescInfoData{name='" + this.name + "', count=" + this.count + ", descriptionText='"
                + this.descriptionText + "', version=" + this.version + ", emotions=" + this.emotions + '}';
    }
}