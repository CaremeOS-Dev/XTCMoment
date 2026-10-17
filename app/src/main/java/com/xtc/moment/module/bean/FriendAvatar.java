package com.xtc.moment.module.bean;

/** One server avatar returned by {@code /extra/getFriendAvatar}. */
public class FriendAvatar {

    private String friendIcon;
    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getFriendIcon() {
        return this.friendIcon;
    }

    public void setFriendIcon(String friendIcon) {
        this.friendIcon = friendIcon;
    }
}
