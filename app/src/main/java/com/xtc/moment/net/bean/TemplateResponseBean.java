package com.xtc.moment.net.bean;

import com.xtc.moment.db.bean.DbTemplate;

import java.util.List;

/** A page of moment templates. */
public class TemplateResponseBean {
    private List<DbTemplate> resources;
    private long total;
    private long updateId;

    public long getUpdateId() {
        return this.updateId;
    }

    public void setUpdateId(long updateId) {
        this.updateId = updateId;
    }

    public long getTotal() {
        return this.total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<DbTemplate> getResources() {
        return this.resources;
    }

    public void setResources(List<DbTemplate> resources) {
        this.resources = resources;
    }
}
