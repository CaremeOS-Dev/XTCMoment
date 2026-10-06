package com.xtc.moment.module.personalinfo.net.bean;

import java.util.List;

/**
 * 个人信息与点赞规则返回体。
 */
public class PersonalInfoAndLikeRuleResponse {

    private String signature;
    private List<LikeRule> likeRule;
    private int likeLimit;
    private int likes;
    private int restLikes;
    private String fuzzyLikes;
    private int likesLimitNumber;

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public List<LikeRule> getLikeRule() {
        return this.likeRule;
    }

    public void setLikeRule(List<LikeRule> likeRule) {
        this.likeRule = likeRule;
    }

    public int getLikeLimit() {
        return this.likeLimit;
    }

    public void setLikeLimit(int likeLimit) {
        this.likeLimit = likeLimit;
    }

    public int getLikes() {
        return this.likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getRestLikes() {
        return this.restLikes;
    }

    public void setRestLikes(int restLikes) {
        this.restLikes = restLikes;
    }

    public int getLikesLimitNumber() {
        return this.likesLimitNumber;
    }

    public void setLikesLimitNumber(int likesLimitNumber) {
        this.likesLimitNumber = likesLimitNumber;
    }

    public void setFuzzyLikes(String fuzzyLikes) {
        this.fuzzyLikes = fuzzyLikes;
    }

    public String getFuzzyLikes() {
        return this.fuzzyLikes;
    }

    @Override
    public String toString() {
        return "PersonalInfoAndLikeRuleResponse{signature='" + this.signature + "', likeRule=" + this.likeRule
                + ", likeLimit=" + this.likeLimit + ", likes=" + this.likes + ", restLikes=" + this.restLikes
                + ", fuzzyLikes='" + this.fuzzyLikes + "', likesLimitNumber=" + this.likesLimitNumber + '}';
    }
}