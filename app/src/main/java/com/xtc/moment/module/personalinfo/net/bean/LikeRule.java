package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 点赞规则配置。
 */
public class LikeRule {

    private int minLevel;
    private int maxLevel;
    private int times;

    public int getMinLevel() {
        return this.minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getMaxLevel() {
        return this.maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getTimes() {
        return this.times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    @Override
    public String toString() {
        return "LikeRule{minLevel=" + this.minLevel + ", maxLevel=" + this.maxLevel + ", times=" + this.times + '}';
    }
}