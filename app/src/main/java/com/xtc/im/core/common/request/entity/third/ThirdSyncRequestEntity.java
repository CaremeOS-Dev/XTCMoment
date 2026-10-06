package com.xtc.im.core.common.request.entity.third;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import com.xtc.im.core.common.request.entity.RequestEntity;

@CommandValue(113)
public class ThirdSyncRequestEntity extends RequestEntity {

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

    @TagValue(14)
    private int pageSize;

    @Deprecated
    @TagValue(15)
    private int mode;

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

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Deprecated
    public int getMode() {
        return this.mode;
    }

    @Deprecated
    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "ThirdSyncRequestEntity{RID=" + this.RID + ", registId=" + this.registId + ", appKey='" + this.appKey + "'" + ", alias='" + this.alias + "'" + ", syncKey=" + this.syncKey + ", pageSize=" + this.pageSize + ", mode=" + this.mode + "}";
    }
}
