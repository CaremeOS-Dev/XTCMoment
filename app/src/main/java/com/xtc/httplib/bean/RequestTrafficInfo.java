package com.xtc.httplib.bean;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/** Tracks the traffic consumed by the current request. */
public class RequestTrafficInfo {
    private AtomicLong startTime = new AtomicLong(System.currentTimeMillis());
    private AtomicLong totalTraffic = new AtomicLong(0);
    private AtomicBoolean hadRecord = new AtomicBoolean(false);

    public long getStartTime() {
        return this.startTime.get();
    }

    public void setStartTime(long startTime) {
        this.startTime.addAndGet(startTime);
    }

    public long getTotalTraffic() {
        return this.totalTraffic.get();
    }

    public void setTotalTraffic(long totalTraffic) {
        this.totalTraffic.addAndGet(totalTraffic);
    }

    public void addTotalTraffic(long traffic) {
        this.totalTraffic.addAndGet(traffic);
    }

    public boolean getHadRecord() {
        return this.hadRecord.get();
    }

    public void setHadRecord(boolean hadRecord) {
        this.hadRecord.getAndSet(hadRecord);
    }

    @Override
    public String toString() {
        return "RequestTrafficInfo{startTime=" + this.startTime + ", totalTraffic=" + this.totalTraffic
                + ", hadRecord=" + this.hadRecord + '}';
    }
}