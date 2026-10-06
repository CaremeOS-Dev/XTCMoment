package com.xtc.system.account.bean;

import java.util.List;

/** Feature-switch record queried from the fundata provider. */
public class FunSwitch {
    private String appPackage;
    private String extend;
    private List<NetFunItem> item;
    private Integer switchId;
    private Integer switchStatus;
    private Integer updateFrom;
    private Integer updateStatus;

    public Integer getSwitchId() {
        return this.switchId;
    }

    public void setSwitchId(Integer switchId) {
        this.switchId = switchId;
    }

    public String getAppPackage() {
        return this.appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }

    public Integer getSwitchStatus() {
        return this.switchStatus;
    }

    public void setSwitchStatus(Integer switchStatus) {
        this.switchStatus = switchStatus;
    }

    public Integer getUpdateStatus() {
        return this.updateStatus;
    }

    public void setUpdateStatus(Integer updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getExtend() {
        return this.extend;
    }

    public void setExtend(String extend) {
        this.extend = extend;
    }

    public Integer getUpdateFrom() {
        return this.updateFrom;
    }

    public void setUpdateFrom(Integer updateFrom) {
        this.updateFrom = updateFrom;
    }

    public List<NetFunItem> getItem() {
        return this.item;
    }

    public void setItem(List<NetFunItem> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ImFunSwitch{switchId=" + this.switchId + ", appPackage=\'" + this.appPackage + "\', switchStatus="
                + this.switchStatus + ", updateStatus=" + this.updateStatus + ", extend=\'" + this.extend
                + "\', updateFrom=" + this.updateFrom + ", item=" + this.item + '}';
    }
}