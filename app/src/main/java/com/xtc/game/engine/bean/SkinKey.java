package com.xtc.game.engine.bean;

/**
 * 皮肤唯一键：由套装名、插槽下标与附件名共同确定。
 */
public class SkinKey {

    public String suitName;
    public int slotIndex;
    public String attachmentName;
    public int hashCode;

    public SkinKey(String suitName, int slotIndex, String attachmentName) {
        set(suitName, slotIndex, attachmentName);
    }

    public void set(String suitName, int slotIndex, String attachmentName) {
        if (attachmentName == null) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        this.suitName = suitName;
        this.slotIndex = slotIndex;
        this.attachmentName = attachmentName;
        this.hashCode = ((attachmentName.hashCode() + 31) * 31) + suitName.hashCode() + 31 + slotIndex;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        SkinKey other = (SkinKey) obj;
        return this.slotIndex == other.slotIndex
                && this.attachmentName.equals(other.attachmentName)
                && this.suitName.equals(other.suitName);
    }

    @Override
    public String toString() {
        return this.suitName + "(" + this.slotIndex + ":" + this.attachmentName + ")  hashCode:" + this.hashCode;
    }
}