package com.xtc.virtualselfapi.bean;

/**
 * 装扮状态：技能卡、消耗品与到期时间等。
 */
public class State {

    private int cardId;
    private int cardItemId;
    private long costunmeId;
    private long hour;
    private long techTime;

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public int getCardId() {
        return this.cardId;
    }

    public void setCardItemId(int cardItemId) {
        this.cardItemId = cardItemId;
    }

    public int getCardItemId() {
        return this.cardItemId;
    }

    public void setTechTime(long techTime) {
        this.techTime = techTime;
    }

    public long getTechTime() {
        return this.techTime;
    }

    public long getCostunmeId() {
        return this.costunmeId;
    }

    public void setCostunmeId(long costunmeId) {
        this.costunmeId = costunmeId;
    }

    public long getHour() {
        return this.hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    @Override
    public String toString() {
        return "State{cardId=" + this.cardId + ", cardItemId=" + this.cardItemId + ", techTime=" + this.techTime
                + ", costunmeId=" + this.costunmeId + ", hour=" + this.hour + '}';
    }
}