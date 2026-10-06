package com.xtc.httplib.bean;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Tracks how often the same url is requested. */
public class FrequentRequestInfo {
    private boolean isRequesting = false;
    private List<Long> requestTimes = new CopyOnWriteArrayList();
    private String requestUrl;

    public String getRequestUrl() {
        return this.requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public boolean isRequesting() {
        return this.isRequesting;
    }

    public void setRequesting(boolean requesting) {
        this.isRequesting = requesting;
    }

    public List<Long> getRequestTimes() {
        return this.requestTimes;
    }

    public void setRequestTimes(List<Long> requestTimes) {
        this.requestTimes = requestTimes;
    }

    @Override
    public String toString() {
        return "FrequentRequestInfo{requestUrl=\'" + this.requestUrl + "\', isRequesting=" + this.isRequesting
                + ", requestTimes=" + this.requestTimes + '}';
    }
}