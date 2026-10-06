package com.xtc.moment.module.personalinfo.net.bean;

/**
 * 删除好友请求参数。
 */
public class DeleteFriendParam {

    private String watchId;
    private String friendId;

    public DeleteFriendParam() {
    }

    public DeleteFriendParam(String watchId, String friendId) {
        this.watchId = watchId;
        this.friendId = friendId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getFriendId() {
        return this.friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    @Override
    public String toString() {
        return "DeleteFriendParam{watchId='" + this.watchId + "', friendId='" + this.friendId + "'}";
    }
}