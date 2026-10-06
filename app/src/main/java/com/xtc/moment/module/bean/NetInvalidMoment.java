package com.xtc.moment.module.bean;

import java.util.List;

/**
 * 服务端下发的动态失效通知：类型、动作以及动态/父级 id 列表。
 */
public class NetInvalidMoment {

    public static final String MOMENTIDS = "momentIds";
    public static final String PARENTIDS = "parentIds";

    private int type;
    private String action;
    private List<String> momentIds;
    private List<String> parentIds;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getMomentIds() {
        return this.momentIds;
    }

    public void setMomentIds(List<String> momentIds) {
        this.momentIds = momentIds;
    }

    public List<String> getParentIds() {
        return this.parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    @Override
    public String toString() {
        return "NetInvalidMoment{type=" + this.type + ", action='" + this.action + "', momentIds=" + this.momentIds
                + ", parentIds=" + this.parentIds + '}';
    }
}