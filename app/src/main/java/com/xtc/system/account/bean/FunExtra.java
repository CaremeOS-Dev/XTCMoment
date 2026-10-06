package com.xtc.system.account.bean;

/** Extra payload attached to a feature-switch item. */
public class FunExtra {
    private String appPackage;
    private String extra;
    private Integer itemId;
    private Integer type;

    public String getAppPackage() {
        return this.appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Integer getItemId() {
        return this.itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExtra() {
        return this.extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "DbFunExtra{, appPackage=\'" + this.appPackage + "\', itemId=" + this.itemId + ", type=" + this.type
                + ", extra=\'" + this.extra + "\'}";
    }
}