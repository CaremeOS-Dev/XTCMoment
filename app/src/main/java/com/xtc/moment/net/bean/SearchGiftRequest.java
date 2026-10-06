package com.xtc.moment.net.bean;

/** Request body for querying gifts on a moment. */
public class SearchGiftRequest {
    private int count;
    private String friendId;
    private Integer gift;
    private String momentId;
    private String watchId;

    public void setGift(int gift) {
        this.gift = Integer.valueOf(gift);
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public int getGift() {
        return this.gift.intValue();
    }

    public String getFriendId() {
        return this.friendId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public int getCount() {
        return this.count;
    }

    public String getMomentId() {
        return this.momentId;
    }
}
