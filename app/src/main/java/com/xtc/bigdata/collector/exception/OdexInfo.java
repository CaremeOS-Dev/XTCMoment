package com.xtc.bigdata.collector.exception;

/**
 * Odex 采集状态信息。
 */
public class OdexInfo {

    private String currentDate;
    private long lastDealOdexTime;
    private int triggerCount;

    public OdexInfo(String currentDate, int triggerCount) {
        this.currentDate = currentDate;
        this.triggerCount = triggerCount;
    }

    public long getLastDealOdexTime() {
        return this.lastDealOdexTime;
    }

    public void setLastDealOdexTime(long lastDealOdexTime) {
        this.lastDealOdexTime = lastDealOdexTime;
    }

    public String getCurrentDate() {
        return this.currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public int getTriggerCount() {
        return this.triggerCount;
    }

    public void setTriggerCount(int triggerCount) {
        this.triggerCount = triggerCount;
    }

    @Override
    public String toString() {
        return "{\"OdexInfo\":{\"lastDealOdexTime\":" + this.lastDealOdexTime + ",\"currentDate\":\"" + this.currentDate + "\",\"triggerCount\":" + this.triggerCount + "}}";
    }
}