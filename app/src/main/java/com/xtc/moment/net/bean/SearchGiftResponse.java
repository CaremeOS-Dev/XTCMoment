package com.xtc.moment.net.bean;

import java.util.List;

/** Gift counts and history for a moment. */
public class SearchGiftResponse {
    private int egg;
    private int flower;
    private List<GiftsEntity> gifts;
    private boolean sendegg;
    private boolean sendflower;

    public void setSendflower(boolean sendflower) {
        this.sendflower = sendflower;
    }

    public void setEgg(int egg) {
        this.egg = egg;
    }

    public void setSendegg(boolean sendegg) {
        this.sendegg = sendegg;
    }

    public void setFlower(int flower) {
        this.flower = flower;
    }

    public void setGifts(List<GiftsEntity> gifts) {
        this.gifts = gifts;
    }

    public boolean isSendflower() {
        return this.sendflower;
    }

    public int getEgg() {
        return this.egg;
    }

    public boolean isSendegg() {
        return this.sendegg;
    }

    public int getFlower() {
        return this.flower;
    }

    public List<GiftsEntity> getGifts() {
        return this.gifts;
    }

    /** One gift record. */
    public class GiftsEntity {
        private Long createTime;
        private int gift;
        private String momentId;
        private String replyId;
        private String watchId;

        public GiftsEntity() {
        }

        public void setGift(int gift) {
            this.gift = gift;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public void setWatchId(String watchId) {
            this.watchId = watchId;
        }

        public void setReplyId(String replyId) {
            this.replyId = replyId;
        }

        public void setMomentId(String momentId) {
            this.momentId = momentId;
        }

        public int getGift() {
            return this.gift;
        }

        public Long getCreateTime() {
            return this.createTime;
        }

        public String getWatchId() {
            return this.watchId;
        }

        public String getReplyId() {
            return this.replyId;
        }

        public String getMomentId() {
            return this.momentId;
        }

        @Override
        public String toString() {
            return "GiftsEntity{gift=" + this.gift + ", createTime=" + this.createTime + ", watchId='" + this.watchId + "', replyId='" + this.replyId + "', momentId='" + this.momentId + "'}";
        }
    }

    @Override
    public String toString() {
        return "SearchGiftResponse{sendflower=" + this.sendflower + ", egg=" + this.egg + ", sendegg=" + this.sendegg + ", flower=" + this.flower + ", gifts=" + this.gifts + '}';
    }
}
