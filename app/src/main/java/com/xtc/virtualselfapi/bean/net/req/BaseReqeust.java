package com.xtc.virtualselfapi.bean.net.req;

/**
 * 基础请求，携带 openId。
 */
public class BaseReqeust {

    private String openId;

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}