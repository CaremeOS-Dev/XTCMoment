package com.xtc.moment.module.personalinfo.net.bean;

import java.util.List;

/**
 * 勋章查询返回体。
 */
public class GetBadgeResponse {

    private String watchId;
    private List<BadgeBean> medals;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public List<BadgeBean> getMedals() {
        return this.medals;
    }

    public void setMedals(List<BadgeBean> medals) {
        this.medals = medals;
    }

    @Override
    public String toString() {
        return "GetBadgeResponse{watchId='" + this.watchId + "', medals=" + this.medals + '}';
    }
}