package com.xtc.moment.module.illegal.net.bean.request;

/**
 * 初始化违规信息请求体。
 */
public class InitViolationBean {

    private String watchId;
    private int informSource = 3;

    public InitViolationBean(String watchId) {
        this.watchId = watchId;
    }

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public int getInformSource() {
        return this.informSource;
    }

    public void setInformSource(int informSource) {
        this.informSource = informSource;
    }
}