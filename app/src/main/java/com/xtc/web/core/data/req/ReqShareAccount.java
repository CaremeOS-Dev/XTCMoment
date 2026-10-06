package com.xtc.web.core.data.req;

/** 分享目标账号信息。 */
public class ReqShareAccount {

    private String account;
    private int accountType;

    public int getAccountType() {
        return this.accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "ReqShareAccount{accountType=" + this.accountType + ", account='" + this.account + "'}";
    }
}