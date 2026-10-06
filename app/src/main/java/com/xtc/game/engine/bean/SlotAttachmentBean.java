package com.xtc.game.engine.bean;

import com.esotericsoftware.spine.attachments.Attachment;

/**
 * 插槽附件数据：记录套装名、插槽下标、插槽名与附件对象。
 */
public class SlotAttachmentBean {

    private String suitName;
    private int slotIndex;
    private String slotName;
    private Attachment attachment;

    public SlotAttachmentBean(String suitName, int slotIndex, String slotName, Attachment attachment) {
        this.suitName = suitName;
        this.slotIndex = slotIndex;
        this.slotName = slotName;
        this.attachment = attachment;
    }

    public SlotAttachmentBean() {
    }

    public int getSlotIndex() {
        return this.slotIndex;
    }

    public void setSlotIndex(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public String getSlotName() {
        return this.slotName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public Attachment getAttachment() {
        return this.attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public String getSuitName() {
        return this.suitName;
    }

    public void setSuitName(String suitName) {
        this.suitName = suitName;
    }

    @Override
    public String toString() {
        return "SlotAttachmentBean{suitName='" + this.suitName + "', slotIndex=" + this.slotIndex
                + ", slotName='" + this.slotName + "', attachment=" + this.attachment + '}';
    }
}