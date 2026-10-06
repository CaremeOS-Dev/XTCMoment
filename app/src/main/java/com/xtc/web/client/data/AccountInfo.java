package com.xtc.web.client.data;

/** 手表账号信息，供 H5 展示用户资料。 */
public class AccountInfo {

    private long birthday;
    private String countryCode;
    private int gender;
    private String geniusNumber;
    private int grade;
    private String iconPath;
    private String name;
    private String number;
    private String openID;
    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getOpenID() {
        return this.openID;
    }

    public void setOpenID(String openID) {
        this.openID = openID;
    }

    public String getIconPath() {
        return this.iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public int getGender() {
        return this.gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGrade() {
        return this.grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public long getBirthday() {
        return this.birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getGeniusNumber() {
        return this.geniusNumber;
    }

    public void setGeniusNumber(String geniusNumber) {
        this.geniusNumber = geniusNumber;
    }

    @Override
    public String toString() {
        return "AccountInfo{watchId='" + this.watchId + "', name='" + this.name + "', number='" + this.number
                + "', countryCode='" + this.countryCode + "', openID='" + this.openID + "', iconPath='"
                + this.iconPath + "', gender=" + this.gender + ", grade=" + this.grade + ", birthday="
                + this.birthday + ", geniusNumber='" + this.geniusNumber + "'}";
    }
}