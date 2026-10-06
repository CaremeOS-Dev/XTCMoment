package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(11)
public class SyncInformResponseEntity extends ResponseEntity {

    @Deprecated
    @TagValue(10)
    private long dialogId;

    @TagValue(11)
    private long imAccountId;

    @TagValue(12)
    private long syncKey;

    @Deprecated
    public long getDialogId() {
        return this.dialogId;
    }

    @Deprecated
    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    public long getImAccountId() {
        return this.imAccountId;
    }

    public void setImAccountId(long imAccountId) {
        this.imAccountId = imAccountId;
    }

    public long getSyncKey() {
        return this.syncKey;
    }

    public void setSyncKey(long syncKey) {
        this.syncKey = syncKey;
    }

    @Override
    public String toString() {
        return "SyncInformResponseEntity{dialogId=" + this.dialogId + ", imAccountId=" + this.imAccountId + ", syncKey=" + this.syncKey + "}";
    }
}
