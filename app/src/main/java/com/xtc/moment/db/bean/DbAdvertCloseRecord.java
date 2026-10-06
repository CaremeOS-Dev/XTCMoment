package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** Records adverts the user dismissed so they are not shown again. */
@DatabaseTable(tableName = Constants.TableName.ADVERT_CLOSE_RECORD)
public class DbAdvertCloseRecord {

    public static final String ADVERT_ID = "advertId";

    @DatabaseField
    private String advertId;

    public DbAdvertCloseRecord(String advertId) {
        this.advertId = advertId;
    }

    public DbAdvertCloseRecord() {
    }

    public String getAdvertId() {
        return this.advertId;
    }

    public void setAdvertId(String advertId) {
        this.advertId = advertId;
    }

    @Override
    public String toString() {
        return "DbAdvertCloseRecord{advertId='" + this.advertId + "'}";
    }
}
