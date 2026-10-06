package com.xtc.moment.event;

/**
 * 动态视频播放状态事件。
 */
public class MomentVideoData {

    public static final int SCREEN_LOCK = 1;
    public static final int SCREEN_LOCK_PAUSE_VIDEO = 2;

    private int action;

    public MomentVideoData(int action) {
        this.action = action;
    }

    public int getAction() {
        return this.action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "MomentVideoData{action=" + this.action + '}';
    }
}