package com.xtc.moment.module.report.bean;

import com.xtc.moment.MomentApp;

/**
 * 举报信息查询参数。
 */
public class ReportInformParam {

    private String watchId = MomentApp.getWatchId();
    private String friendWatchId;
    private int informSource;

    public ReportInformParam() {
    }

    public ReportInformParam(String friendWatchId) {
        this.friendWatchId = friendWatchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public String getFriendWatchId() {
        return this.friendWatchId;
    }

    public void setFriendWatchId(String friendWatchId) {
        this.friendWatchId = friendWatchId;
    }

    public int getInformSource() {
        return this.informSource;
    }

    public void setInformSource(int informSource) {
        this.informSource = informSource;
    }
}