package com.xtc.httplib.netstate;

/** Thresholds controlling automatic network switching. */
public class SwitchConfigBean {
    public static final int MAX_DAY_SWITCH_NUM = 2097152;
    public static final long RESET_SWITCH_SHAKE_TIME = 300000;
    public static final long SWITCH_SHAKE_TIME = 60000;

    private int maxDataNum = MAX_DAY_SWITCH_NUM;
    private long shakeTime = SWITCH_SHAKE_TIME;
    private long resetTime = RESET_SWITCH_SHAKE_TIME;

    public static int getMaxDaySwitchNum() {
        return MAX_DAY_SWITCH_NUM;
    }

    public static long getResetSwitchShakeTime() {
        return RESET_SWITCH_SHAKE_TIME;
    }

    public static long getSwitchShakeTime() {
        return SWITCH_SHAKE_TIME;
    }

    public int getMaxDataNum() {
        return this.maxDataNum;
    }

    public void setMaxDataNum(int maxDataNum) {
        this.maxDataNum = maxDataNum;
    }

    public long getShakeTime() {
        return this.shakeTime;
    }

    public void setShakeTime(long shakeTime) {
        this.shakeTime = shakeTime;
    }

    public long getResetTime() {
        return this.resetTime;
    }

    public void setResetTime(long resetTime) {
        this.resetTime = resetTime;
    }

    @Override
    public String toString() {
        return "SwitchConfigBean{maxDataNum=" + this.maxDataNum + ", shakeTime=" + this.shakeTime
                + ", resetTime=" + this.resetTime + '}';
    }
}