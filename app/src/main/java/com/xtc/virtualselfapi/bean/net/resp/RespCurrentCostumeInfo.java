package com.xtc.virtualselfapi.bean.net.resp;

import com.xtc.virtualselfapi.bean.Costume;
import com.xtc.virtualselfapi.bean.State;

/**
 * 当前装扮信息响应。
 */
public class RespCurrentCostumeInfo {

    private Costume costume;
    private State state;

    public Costume getCostume() {
        return this.costume;
    }

    public void setCostume(Costume costume) {
        this.costume = costume;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "RespCurrentCostumeInfo{costume=" + this.costume + ", state=" + this.state + '}';
    }
}