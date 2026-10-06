package com.xtc.moment.module.bean;

/** A friend of the watch account. */
public class Friend {

    private String friendIcon;
    private String name;
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

    public String getFriendIcon() {
        return this.friendIcon;
    }

    public void setFriendIcon(String friendIcon) {
        this.friendIcon = friendIcon;
    }

    @Override
    public String toString() {
        return "Friend{watchId='" + this.watchId + "', name='" + this.name + "', friendIcon='" + this.friendIcon + "'}";
    }
}