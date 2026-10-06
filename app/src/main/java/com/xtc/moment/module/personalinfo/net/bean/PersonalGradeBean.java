package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 个人等级信息。
 */
public class PersonalGradeBean {

    private String watchId;
    private String salutation;
    private int level;
    private int newExp;
    private int score;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getSalutation() {
        return this.salutation;
    }

    public void setSalutation(String salutation) {
        this.salutation = salutation;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getNewExp() {
        return this.newExp;
    }

    public void setNewExp(int newExp) {
        this.newExp = newExp;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "PersonalGradeBean{watchId='" + this.watchId + "', salutation='" + this.salutation + "', level="
                + this.level + ", newExp=" + this.newExp + ", score=" + this.score + '}';
    }
}