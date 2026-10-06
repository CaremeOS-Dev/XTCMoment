package com.xtc.im.core.common.response.entity.third;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;
import com.xtc.im.core.common.response.entity.ResponseEntity;

@CommandValue(114)
public class ThirdSyncResponseEntity extends ResponseEntity {

    @TagValue(1)
    private int RID;

    @TagValue(2)
    private int code;

    @TagValue(3)
    private String desc;

    @TagValue(10)
    private String pkgName;

    @TagValue(11)
    private String alias;

    @TagValue(12)
    private byte[] message;

    @TagValue(13)
    private long syncKey;

    @TagValue(14)
    private String msgId;

    @TagValue(15)
    private long createTime;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPkgName() {
        return this.pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public byte[] getMessage() {
        return this.message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
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

    @Override
    public String toString() {
        return "ThirdSyncResponseEntity{RID=" + this.RID + ", code=" + this.code + ", desc='" + this.desc + "'" + ", pkgName='" + this.pkgName + "'" + ", alias='" + this.alias + "'" + ", message=" + this.message + ", syncKey=" + this.syncKey + ", msgId='" + this.msgId + "'" + ", createTime=" + this.createTime + "}";
    }
}
