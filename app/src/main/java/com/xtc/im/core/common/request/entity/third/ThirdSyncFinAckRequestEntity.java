package com.xtc.im.core.common.request.entity.third;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import com.xtc.im.core.common.request.entity.RequestEntity;

@CommandValue(116)
public class ThirdSyncFinAckRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(10)
    private long registId;

    @TagValue(11)
    private String appKey;

    @TagValue(12)
    private String alias;

    @TagValue(13)
    private long syncKey;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public long getRegistId() {
        return this.registId;
    }

    public void setRegistId(long registId) {
        this.registId = registId;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getSyncKey() {
        return this.syncKey;
    }

    public void setSyncKey(long syncKey) {
        this.syncKey = syncKey;
    }

    @Override
    public String toString() {
        return "ThirdSyncFinAckRequestEntity{RID=" + this.RID + ", registId=" + this.registId + ", appKey='" + this.appKey + "'" + ", alias='" + this.alias + "'" + ", syncKey=" + this.syncKey + "}";
    }
}
