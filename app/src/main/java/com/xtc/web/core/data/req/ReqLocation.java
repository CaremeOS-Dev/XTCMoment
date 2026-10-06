package com.xtc.web.core.data.req;

/** 定位请求，仅携带手表 id。 */
public class ReqLocation {

    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }
}