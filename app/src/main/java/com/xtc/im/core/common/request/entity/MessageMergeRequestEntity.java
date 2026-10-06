package com.xtc.im.core.common.request.entity;

import com.xtc.im.core.common.anotation.CommandValue;
import com.xtc.im.core.common.anotation.TagValue;

@CommandValue(29)
public class MessageMergeRequestEntity extends RequestEntity {

    @TagValue(1)
    private int RID;

    @TagValue(10)
    private byte[] msgMergePackets;

    @Override
    public int getRID() {
        return this.RID;
    }

    @Override
    public void setRID(int RID) {
        this.RID = RID;
    }

    public byte[] getMsgMergePackets() {
        return this.msgMergePackets;
    }

    public void setMsgMergePackets(byte[] msgMergePackets) {
        this.msgMergePackets = msgMergePackets;
    }

    @Override
    public String toString() {
        return "MessageMergeRequestEntity{RID=" + this.RID + ", msgMergePackets=" + this.msgMergePackets + "}";
    }
}
