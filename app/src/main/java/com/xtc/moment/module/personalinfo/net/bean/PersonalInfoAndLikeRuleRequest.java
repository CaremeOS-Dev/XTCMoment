package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 个人信息与点赞规则查询请求。
 */
public class PersonalInfoAndLikeRuleRequest {

    private String watchId;
    private int level;

    public PersonalInfoAndLikeRuleRequest(String watchId, int level) {
        this.watchId = watchId;
        this.level = level;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "PersonalInfoAndLikeRuleRequest{watchId='" + this.watchId + "', level=" + this.level + '}';
    }
}