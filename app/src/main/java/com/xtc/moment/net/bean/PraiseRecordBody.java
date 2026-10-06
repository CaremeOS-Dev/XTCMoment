package com.xtc.moment.net.bean;

import java.util.List;

/** Request body for the like-record query. */
public class PraiseRecordBody {
    private List<String> momentIds;
    private String momentWatchId;

    public String getMomentWatchId() {
        return this.momentWatchId;
    }

    public void setMomentWatchId(String momentWatchId) {
        this.momentWatchId = momentWatchId;
    }

    public List<String> getMomentIds() {
        return this.momentIds;
    }

    public void setMomentIds(List<String> momentIds) {
        this.momentIds = momentIds;
    }
}
