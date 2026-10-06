package com.xtc.system.account.bean;

/** Account information of the watch owner. */
public class WatchInfo {
    private Long birthday;
    private String gender;
    private String grade;
    private String innerModel;
    private String model;
    private String name;
    private String number;
    private String shortNumber;

    public WatchInfo() {
    }

    public WatchInfo(String name, String number, String shortNumber, String gender, String grade, Long birthday) {
        this.name = name;
        this.number = number;
        this.shortNumber = shortNumber;
        this.gender = gender;
        this.grade = grade;
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "WatchInfo{, number=\'" + this.number + "\', shortNumber=\'" + this.shortNumber + "\', name=\'"
                + this.name + "\', gender=" + this.gender + ", grade=" + this.grade + ", birthday=" + this.birthday + '}';
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getShortNumber() {
        return this.shortNumber;
    }

    public void setShortNumber(String shortNumber) {
        this.shortNumber = shortNumber;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return this.gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Long getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getInnerModel() {
        return this.innerModel;
    }

    public void setInnerModel(String innerModel) {
        this.innerModel = innerModel;
    }
}