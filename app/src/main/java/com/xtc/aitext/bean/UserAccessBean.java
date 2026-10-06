package com.xtc.aitext.bean;

/**
 * 用户权益项。
 */
public class UserAccessBean {

    private String accessType;
    private String accessDescription;
    private String isUse;
    private String accessName;
    private String watchId;
    private String totalTimes;
    private String tagText;
    private int sort;
    private String subAccessDescription;
    private ButtonDescriptionBean buttonDescription;

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public String getAccessDescription() {
        return accessDescription;
    }

    public void setAccessDescription(String accessDescription) {
        this.accessDescription = accessDescription;
    }

    public String getIsUse() {
        return isUse;
    }

    public void setIsUse(String isUse) {
        this.isUse = isUse;
    }

    public String getAccessName() {
        return accessName;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    public String getWatchId() {
        return watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getTotalTimes() {
        return totalTimes;
    }

    public void setTotalTimes(String totalTimes) {
        this.totalTimes = totalTimes;
    }

    public ButtonDescriptionBean getButtonDescription() {
        return buttonDescription;
    }

    public void setButtonDescription(ButtonDescriptionBean buttonDescription) {
        this.buttonDescription = buttonDescription;
    }

    public String getTagText() {
        return tagText;
    }

    public void setTagText(String tagText) {
        this.tagText = tagText;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getSubAccessDescription() {
        return subAccessDescription;
    }

    public void setSubAccessDescription(String subAccessDescription) {
        this.subAccessDescription = subAccessDescription;
    }

    @Override
    public String toString() {
        return "UserAccessBean{accessType='" + accessType + "', accessDescription='" + accessDescription
                + "', isUse='" + isUse + "', accessName='" + accessName + "', watchId='" + watchId
                + "', totalTimes='" + totalTimes + "', tagText='" + tagText + "', sort=" + sort
                + ", subAccessDescription='" + subAccessDescription + "', buttonDescription=" + buttonDescription + '}';
    }
}