package com.xtc.moment.net.bean;

/** Parameters for querying report data. */
public class ReportDataParam {
    private Integer type;
    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }

    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ReportDataParam{watchId='" + this.watchId + "', type=" + this.type + '}';
    }
}
