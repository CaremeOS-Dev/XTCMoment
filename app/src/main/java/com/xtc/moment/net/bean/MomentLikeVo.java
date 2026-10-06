package com.xtc.moment.net.bean;

/** A like record enriched with the liker's display name. */
public class MomentLikeVo extends MomentLike {
    private String watchName;

    public String getWatchName() {
        return this.watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    @Override
    public String toString() {
        return "MomentLikeVo{watchName='" + this.watchName + "'momentLike=" + super.toString() + "'}";
    }
}
