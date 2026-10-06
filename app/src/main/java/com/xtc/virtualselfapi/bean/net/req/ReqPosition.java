package com.xtc.virtualselfapi.bean.net.req;

import java.util.List;

/**
 * 查询装扮布局请求。
 */
public class ReqPosition {

    private List<Integer> ornamentIdList;
    private int suitId;

    public int getSuitId() {
        return this.suitId;
    }

    public void setSuitId(int suitId) {
        this.suitId = suitId;
    }

    public List<Integer> getOrnamentIdList() {
        return this.ornamentIdList;
    }

    public void setOrnamentIdList(List<Integer> ornamentIdList) {
        this.ornamentIdList = ornamentIdList;
    }

    @Override
    public String toString() {
        return "ReqPosition{suitId=" + this.suitId + ", ornamentIdList=" + this.ornamentIdList + '}';
    }
}