package com.xtc.moment.module.bean;

import com.xtc.moment.db.bean.DbMoment;

/** Fired when a location moment is starred. */
public class LbsStarEvent {

    private final DbMoment dbMoment;

    public LbsStarEvent(DbMoment dbMoment) {
        this.dbMoment = dbMoment;
    }

    public DbMoment getDbMoment() {
        return this.dbMoment;
    }
}