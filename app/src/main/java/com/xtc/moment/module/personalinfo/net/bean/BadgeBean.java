package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 勋章信息。
 */
public class BadgeBean {

    private String medalId;
    private String icon;

    public String getMedalId() {
        return this.medalId;
    }

    public void setMedalId(String medalId) {
        this.medalId = medalId;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "BadgeBean{medalId='" + this.medalId + "', icon='" + this.icon + "'}";
    }
}