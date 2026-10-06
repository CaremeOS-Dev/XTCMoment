package com.xtc.moment.module.bean;

import java.util.List;

/**
 * 查询动态可见范围的请求体，只携带动态 id 列表。
 */
public class FriendsVisibleBeanReq {

    private List<String> momentIds;

    public List<String> getMomentIds() {
        return this.momentIds;
    }

    public void setMomentIds(List<String> momentIds) {
        this.momentIds = momentIds;
    }

    @Override
    public String toString() {
        return "FriendsVisibleBeanReq{momentIds=" + this.momentIds + '}';
    }
}