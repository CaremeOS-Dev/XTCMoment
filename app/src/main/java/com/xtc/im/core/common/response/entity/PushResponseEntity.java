package com.xtc.im.core.common.response.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(35)
public class PushResponseEntity extends ResponseEntity {

    @TagValue(10)
    private long dialogId;

    @TagValue(11)
    private long imAccountId;

    @TagValue(12)
    private long registId;

    @TagValue(13)
    private int msgType;

    @TagValue(14)
    private byte[] msg;

    @TagValue(15)
    private long syncKey;

    @TagValue(16)
    private String msgId;

    @TagValue(17)
    private long createTime;

    @TagValue(18)
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

    public int getMsgType() {
        return this.msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public byte[] getMsg() {
        return this.msg;
    }

    public void setMsg(byte[] msg) {
        this.msg = msg;
    }

    public long getSyncKey() {
        return this.syncKey;
    }

    public void setSyncKey(long syncKey) {
        this.syncKey = syncKey;
    }

    public String getMsgId() {
        return this.msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getContentType() {
        return this.contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "PushResponseEntity{dialogId=" + this.dialogId + ", imAccountId=" + this.imAccountId + ", registId=" + this.registId + ", msgType=" + this.msgType + ", msg=" + this.msg + ", syncKey=" + this.syncKey + ", msgId='" + this.msgId + "'" + ", createTime=" + this.createTime + ", contentType=" + this.contentType + "}";
    }
}
