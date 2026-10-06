package com.xtc.moment.module.bean;

/** Friend-change push received from the contact service. */
public class FriendPush {

    public static final int ACTION_GROUP_ADD = 4;
    public static final int ACTION_REMOTE_ADD = 3;
    public static final int ACTION_REMOTE_CHANGE = 1;
    public static final int ACTION_REMOTE_DEL = 2;

    private int action;
    private String friendId;
    private String friendName;
    private String icon;
    private String id;
    private String watchId;

    public String getFriendName() {
        return this.friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getFriendId() {
        return this.friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getAction() {
        return this.action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FriendPush{friendName='" + this.friendName + "', icon='" + this.icon + "', friendId='" + this.friendId
                + "', watchId='" + this.watchId + "', action=" + this.action + ", id='" + this.id + "'}";
    }
}