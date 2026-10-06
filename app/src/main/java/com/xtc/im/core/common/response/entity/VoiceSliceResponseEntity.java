package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(24)
public class VoiceSliceResponseEntity extends ResponseEntity {

    @TagValue(10)
    private long dialogId;

    @TagValue(11)
    private long imAccountId;

    @TagValue(12)
    private long registId;

    @TagValue(13)
    private byte[] msg;

    @TagValue(14)
    private long reciverId;

    @TagValue(15)
    private int contentType;

    public long getDialogId() {
        return this.dialogId;
    }

    public void setDialogId(long dialogId) {
        this.dialogId = dialogId;
    }

    public long getImAccountId() {
        return this.imAccountId;
    }

    public void setImAccountId(long imAccountId) {
        this.imAccountId = imAccountId;
    }

    public long getRegistId() {
        return this.registId;
    }

    public void setRegistId(long registId) {
        this.registId = registId;
    }

    public byte[] getMsg() {
        return this.msg;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }

    public long getReciverId() {
        return this.reciverId;
    }

    public void setReciverId(long reciverId) {
        this.reciverId = reciverId;
    }

    public int getContentType() {
        return this.contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "VoiceSliceResponseEntity{dialogId=" + this.dialogId + ", imAccountId=" + this.imAccountId + ", registId=" + this.registId + ", msg=" + this.msg + ", reciverId=" + this.reciverId + ", contentType=" + this.contentType + "}";
    }
}
