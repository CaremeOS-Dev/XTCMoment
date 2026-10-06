package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 个人资料返回体。
 */
public class PersonalInfoResponse {

    private String mobileId;
    private String watchId;
    private String bindNumber;
    private String geniusNumber;
    private int score;
    private int friends;
    private int contacts;
    private int level;
    private String icon;
    private String name;
    private int gender = 3;
    private long birthday;
    private int authId;

    public String getMobileId() {
        return this.mobileId;
    }

    public void setMobileId(String mobileId) {
        this.mobileId = mobileId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getBindNumber() {
        return this.bindNumber;
    }

    public void setBindNumber(String bindNumber) {
        this.bindNumber = bindNumber;
    }

    public String getGeniusNumber() {
        return this.geniusNumber;
    }

    public void setGeniusNumber(String geniusNumber) {
        this.geniusNumber = geniusNumber;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFriends() {
        return this.friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public int getContacts() {
        return this.contacts;
    }

    public void setContacts(int contacts) {
        this.contacts = contacts;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getBirthday() {
        return this.birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public int getAuthId() {
        return this.authId;
    }

    public void setAuthId(int authId) {
        this.authId = authId;
    }

    @Override
    public String toString() {
        return "PersonalInfoResponse{mobileId='" + this.mobileId + "', watchId='" + this.watchId + "', bindNumber='"
                + this.bindNumber + "', geniusNumber='" + this.geniusNumber + "', score=" + this.score + ", friends="
                + this.friends + ", contacts=" + this.contacts + ", level=" + this.level + ", icon='" + this.icon
                + "', name='" + this.name + "', gender=" + this.gender + ", birthday=" + this.birthday + ", authId="
                + this.authId + '}';
    }
}