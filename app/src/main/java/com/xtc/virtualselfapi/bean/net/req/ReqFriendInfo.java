package com.xtc.virtualselfapi.bean.net.req;

import java.util.List;

/**
 * 查询好友虚拟形象请求。
 */
public class ReqFriendInfo {

    private List<String> fOpenIdList;
    private String openId;

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public List<String> getfOpenIdList() {
        return this.fOpenIdList;
    }

    public void setfOpenIdList(List<String> fOpenIdList) {
        this.fOpenIdList = fOpenIdList;
    }

    @Override
    public String toString() {
        return "ReqFriendInfo{openId='" + this.openId + "', fOpenIdList=" + this.fOpenIdList + '}';
    }
}