package com.xtc.moment.net.bean;

/** Paging request for the moment template list. */
public class TemplateRequestBean {
    private long from;
    private long size;
    private long updateId;

    public long getUpdateId() {
        return this.updateId;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }

    public long getFrom() {
        return this.from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
