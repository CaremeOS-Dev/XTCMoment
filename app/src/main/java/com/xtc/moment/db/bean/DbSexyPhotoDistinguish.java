package com.xtc.moment.db.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import com.xtc.moment.db.Constants;

/** Remembers that a photo was classified as NSFW, keyed by message id. */
@DatabaseTable(tableName = Constants.TableName.MOMENT_SEXY_PHOTO_DISTINGUISH)
public class DbSexyPhotoDistinguish {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField
    private String key;

    @DatabaseField
    private String msgId;

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return "DbSexyPhotoDistinguish{msgId='" + this.msgId + "', key='" + this.key + "'}";
    }
}
