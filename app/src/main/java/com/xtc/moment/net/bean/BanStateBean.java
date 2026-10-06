package com.xtc.moment.net.bean;

/** Server-reported ban state. */
public class BanStateBean {
    private boolean bannedToPost;

    public Boolean getBannedToPost() {
        return Boolean.valueOf(this.bannedToPost);
    }

    public void setBannedToPost(Boolean bannedToPost) {
        this.bannedToPost = bannedToPost.booleanValue();
    }

    @Override
    public String toString() {
        return "BanStateBean{bannedToPost=" + this.bannedToPost + "'}";
    }
}
