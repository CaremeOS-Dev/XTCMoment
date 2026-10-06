package com.xtc.system.account.bean;

import java.util.ArrayList;
import java.util.List;

/** IM account identifiers stored by the launcher. */
public class ImAccountInfo {
    private String accountId;
    private Long familyChatDialogId;
    private Long imAccountId;
    private Long locationDialogId;
    private Long noticeDialogId;
    private Long optFamilyDialogId;
    private Long readDialogId;
    private Long receiptPushDialogId;
    private Long receiveDialogId;
    private Long schoolGuardDialogId;
    private Long singlePushDialogId;
    private Long smsDialogId;

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Long getImAccountId() {
        return this.imAccountId;
    }

    public void setImAccountId(Long imAccountId) {
        this.imAccountId = imAccountId;
    }

    public Long getSinglePushDialogId() {
        return this.singlePushDialogId;
    }

    public void setSinglePushDialogId(Long singlePushDialogId) {
        this.singlePushDialogId = singlePushDialogId;
    }

    public Long getReceiveDialogId() {
        return this.receiveDialogId;
    }

    public void setReceiveDialogId(Long receiveDialogId) {
        this.receiveDialogId = receiveDialogId;
    }

    public Long getReceiptPushDialogId() {
        return this.receiptPushDialogId;
    }

    public void setReceiptPushDialogId(Long receiptPushDialogId) {
        this.receiptPushDialogId = receiptPushDialogId;
    }

    public Long getReadDialogId() {
        return this.readDialogId;
    }

    public void setReadDialogId(Long readDialogId) {
        this.readDialogId = readDialogId;
    }

    public Long getFamilyChatDialogId() {
        return this.familyChatDialogId;
    }

    public void setFamilyChatDialogId(Long familyChatDialogId) {
        this.familyChatDialogId = familyChatDialogId;
    }

    public Long getOptFamilyDialogId() {
        return this.optFamilyDialogId;
    }

    public void setOptFamilyDialogId(Long optFamilyDialogId) {
        this.optFamilyDialogId = optFamilyDialogId;
    }

    public Long getLocationDialogId() {
        return this.locationDialogId;
    }

    public void setLocationDialogId(Long locationDialogId) {
        this.locationDialogId = locationDialogId;
    }

    public Long getSchoolGuardDialogId() {
        return this.schoolGuardDialogId;
    }

    public void setSchoolGuardDialogId(Long schoolGuardDialogId) {
        this.schoolGuardDialogId = schoolGuardDialogId;
    }

    public Long getNoticeDialogId() {
        return this.noticeDialogId;
    }

    public void setNoticeDialogId(Long noticeDialogId) {
        this.noticeDialogId = noticeDialogId;
    }

    public Long getSmsDialogId() {
        return this.smsDialogId;
    }

    public void setSmsDialogId(Long smsDialogId) {
        this.smsDialogId = smsDialogId;
    }

    @Override
    public String toString() {
        return "ImAccountInfo{accountId=\'" + this.accountId + "\', imAccountId=" + this.imAccountId
                + ", singlePushDialogId=" + this.singlePushDialogId + ", receiveDialogId=" + this.receiveDialogId
                + ", receiptPushDialogId=" + this.receiptPushDialogId + ", readDialogId=" + this.readDialogId
                + ", familyChatDialogId=" + this.familyChatDialogId + ", optFamilyDialogId=" + this.optFamilyDialogId
                + ", locationDialogId=" + this.locationDialogId + ", schoolGuardDialogId=" + this.schoolGuardDialogId
                + ", noticeDialogId=" + this.noticeDialogId + ", smsDialogId=" + this.smsDialogId + '}';
    }

    /** All dialog ids as a list. */
    public List<Long> toList() {
        ArrayList<Long> list = new ArrayList<>();
        list.add(this.imAccountId);
        list.add(this.singlePushDialogId);
        list.add(this.receiveDialogId);
        list.add(this.receiptPushDialogId);
        list.add(this.readDialogId);
        list.add(this.familyChatDialogId);
        list.add(this.optFamilyDialogId);
        list.add(this.locationDialogId);
        list.add(this.schoolGuardDialogId);
        list.add(this.noticeDialogId);
        list.add(this.smsDialogId);
        return list;
    }
}