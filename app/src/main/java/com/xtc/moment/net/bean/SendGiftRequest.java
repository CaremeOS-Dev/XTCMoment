package com.xtc.moment.net.bean;

/** Request body for sending a gift. */
public class SendGiftRequest {
    private String friendId;
    private int gift;
    private String momentId;
    private String watchId;

    public SendGiftRequest() {
    }

    public SendGiftRequest(String momentId, String watchId, String friendId, int gift) {
        this.momentId = momentId;
        this.watchId = watchId;
        this.friendId = friendId;
        this.gift = gift;
    }

    public String getMomentId() {
        return this.momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
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

    public int getGift() {
        return this.gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }
}
