package com.xtc.moment.third.bean;

/**
 * 动态预览行为埋点数据。
 */
public class PushBehaviorBean {

    public static final int BEHAVIOR_SKIPED = 0;
    public static final int BEHAVIOR_PREVIEWED = 1;

    private String momentId;
    private int behavior;

    public PushBehaviorBean(String momentId, int behavior) {
        this.momentId = momentId;
        this.behavior = behavior;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public int getBehavior() {
        return this.behavior;
    }

    public void setBehavior(int behavior) {
        this.behavior = behavior;
    }

    @Override
    public String toString() {
        return "MomentBehaviorBean{momentId='" + this.momentId + "', behavior=" + this.behavior + '}';
    }
}