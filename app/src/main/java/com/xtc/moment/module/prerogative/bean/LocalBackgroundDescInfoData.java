package com.xtc.moment.module.prerogative.bean;

import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;

import java.util.List;

/**
 * 本地背景特效资源描述。
 */
public class LocalBackgroundDescInfoData {

    private List<DbMomentPrerogativeBackground> backgrounds;
    private String name;
    private int count;
    private String descriptionText;
    private int version;

    public void setBackgrounds(List<DbMomentPrerogativeBackground> backgrounds) {
        this.backgrounds = backgrounds;
    }

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

    public List<DbMomentPrerogativeBackground> getBackgrounds() {
        return this.backgrounds;
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

    @Override
    public String toString() {
        return "LocalBackgroundDescInfoData{backgrounds=" + this.backgrounds + ", name='" + this.name + "', count="
                + this.count + ", descriptionText='" + this.descriptionText + "', version=" + this.version + '}';
    }
}