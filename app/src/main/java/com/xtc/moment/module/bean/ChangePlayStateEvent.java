package com.xtc.moment.module.bean;

/** Fired when a video play state changes. */
public class ChangePlayStateEvent {

    public static final int STATE_PAUSE = 2;
    public static final int STATE_PLAY = 1;

    private String desc;
    private int playState;

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getPlayState() {
        return this.playState;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }

    @Override
    public String toString() {
        return "ChangePlayStateEvent{desc='" + this.desc + "', playState=" + this.playState + '}';
    }
}