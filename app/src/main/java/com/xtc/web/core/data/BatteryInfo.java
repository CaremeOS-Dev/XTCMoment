package com.xtc.web.core.data;

/** 手表电量信息：电量百分比、充电状态与温度。 */
public class BatteryInfo {

    private int level;
    private int plugged;
    private int temperature;

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPlugged() {
        return this.plugged;
    }

    public void setPlugged(int plugged) {
        this.plugged = plugged;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "BatteryInfo{level=" + this.level + ", plugged=" + this.plugged + ", temperature=" + this.temperature
                + '}';
    }
}