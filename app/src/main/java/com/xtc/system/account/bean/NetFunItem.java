package com.xtc.system.account.bean;

import java.util.List;

/** Single entry of a feature-switch list. */
public class NetFunItem {
    private String appPackage;
    private List<String> extra;
    private Integer id;
    private Integer old;
    private Integer order;
    private String subTitle;
    private String title;
    private Integer type;

    public String getAppPackage() {
        return this.appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return this.subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getOrder() {
        return this.order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOld() {
        return this.old;
    }

    public void setOld(Integer old) {
        this.old = old;
    }

    public List<String> getExtra() {
        return this.extra;
    }

    public void setExtra(List<String> extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "NetFunItem{appPackage=\'" + this.appPackage + "\', id=" + this.id + ", title=\'" + this.title
                + "\', subTitle=\'" + this.subTitle + "\', type=" + this.type + ", order=" + this.order + ", old="
                + this.old + ", extra=" + this.extra + '}';
    }
}