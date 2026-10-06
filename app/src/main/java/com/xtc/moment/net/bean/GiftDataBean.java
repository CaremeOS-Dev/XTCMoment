package com.xtc.moment.net.bean;

/** A gift record with the sender's identity. */
public class GiftDataBean {
    private int giftType;
    private String watchIcon;
    private String watchId;
    private String watchName;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getWatchName() {
        return this.watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getWatchIcon() {
        return this.watchIcon;
    }

    public void setWatchIcon(String watchIcon) {
        this.watchIcon = watchIcon;
    }

    public int getGiftType() {
        return this.giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    @Override
    public String toString() {
        return "GiftDataBean{watchId='" + this.watchId + "', watchName='" + this.watchName + "', watchIcon='" + this.watchIcon + "', giftType=" + this.giftType + '}';
    }
}
