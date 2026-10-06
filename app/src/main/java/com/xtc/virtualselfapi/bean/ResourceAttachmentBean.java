package com.xtc.virtualselfapi.bean;

import java.io.Serializable;

/**
 * 装扮资源附件：记录附件名、所属套装、类型与插槽。
 */
public class ResourceAttachmentBean implements Serializable {

    private String dressAnnexName;
    private String dressAnnexSlot;
    private String dressAnnexType;
    private String dressBelongSuit;

    public String getDressAnnexName() {
        return this.dressAnnexName;
    }

    public void setDressAnnexName(String dressAnnexName) {
        this.dressAnnexName = dressAnnexName;
    }

    public String getDressBelongSuit() {
        return this.dressBelongSuit;
    }

    public void setDressBelongSuit(String dressBelongSuit) {
        this.dressBelongSuit = dressBelongSuit;
    }

    public String getDressAnnexType() {
        return this.dressAnnexType;
    }

    public void setDressAnnexType(String dressAnnexType) {
        this.dressAnnexType = dressAnnexType;
    }

    public String getDressAnnexSlot() {
        return this.dressAnnexSlot;
    }

    public void setDressAnnexSlot(String dressAnnexSlot) {
        this.dressAnnexSlot = dressAnnexSlot;
    }

    @Override
    public String toString() {
        return "ResourceAttachmentBean{dressAnnexName='" + this.dressAnnexName + "', dressBelongSuit='"
                + this.dressBelongSuit + "', dressAnnexType='" + this.dressAnnexType + "', dressAnnexSlot='"
                + this.dressAnnexSlot + "'}";
    }
}