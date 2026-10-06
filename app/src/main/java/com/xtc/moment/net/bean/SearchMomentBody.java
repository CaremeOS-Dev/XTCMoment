package com.xtc.moment.net.bean;

/** Paged request body for the moment list. */
public class SearchMomentBody {

    public static final int COMMENT_COUNT = 5;

    private long begin;
    private int commentPageSize;
    private String currentWatchId;
    private long end;
    private int friend;
    private long from;
    private long lastLikeTime;
    private int searchPermission;
    private long size;
    private String watchId;

    public long getBegin() {
        return this.begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }

    public long getEnd() {
        return this.end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public int getFriend() {
        return this.friend;
    }

    public void setFriend(int friend) {
        this.friend = friend;
    }

    public long getFrom() {
        return this.from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getLastLikeTime() {
        return this.lastLikeTime;
    }

    public void setLastLikeTime(long lastLikeTime) {
        this.lastLikeTime = lastLikeTime;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getCurrentWatchId() {
        return this.currentWatchId;
    }

    public void setCurrentWatchId(String currentWatchId) {
        this.currentWatchId = currentWatchId;
    }

    public int getCommentPageSize() {
        return this.commentPageSize;
    }

    public void setCommentPageSize(int commentPageSize) {
        this.commentPageSize = commentPageSize;
    }

    public int getSearchPermission() {
        return this.searchPermission;
    }

    public void setSearchPermission(int searchPermission) {
        this.searchPermission = searchPermission;
    }
}
