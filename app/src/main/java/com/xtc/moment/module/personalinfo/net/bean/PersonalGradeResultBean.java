package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 个人等级成长结果。
 */
public class PersonalGradeResultBean {

    private long timeStamp;
    private int score;
    private int levelGap;
    private int level;
    private int costScore;
    private int nextLevelGap;
    private int getExp;
    private int scoreForExpNum;
    private int restScoreOfExpNum;

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevelGap() {
        return this.levelGap;
    }

    public void setLevelGap(int levelGap) {
        this.levelGap = levelGap;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCostScore() {
        return this.costScore;
    }

    public void setCostScore(int costScore) {
        this.costScore = costScore;
    }

    public int getNextLevelGap() {
        return this.nextLevelGap;
    }

    public void setNextLevelGap(int nextLevelGap) {
        this.nextLevelGap = nextLevelGap;
    }

    public int getGetExp() {
        return this.getExp;
    }

    public void setGetExp(int getExp) {
        this.getExp = getExp;
    }

    public int getScoreForExpNum() {
        return this.scoreForExpNum;
    }

    public void setScoreForExpNum(int scoreForExpNum) {
        this.scoreForExpNum = scoreForExpNum;
    }

    public int getRestScoreOfExpNum() {
        return this.restScoreOfExpNum;
    }

    public void setRestScoreOfExpNum(int restScoreOfExpNum) {
        this.restScoreOfExpNum = restScoreOfExpNum;
    }

    @Override
    public String toString() {
        return "PersonalGradeResultBean{timeStamp=" + this.timeStamp + ", score=" + this.score + ", level=" + this.level
                + ", levelGap=" + this.levelGap + ", nextLevelGap=" + this.nextLevelGap + ", costScore=" + this.costScore
                + ", getExp=" + this.getExp + ", scoreForExpNum=" + this.scoreForExpNum + ", restScoreOfExpNum="
                + this.restScoreOfExpNum + '}';
    }
}