package com.xtc.web.client.data.response;

import com.xtc.web.client.data.FriendInfo;

import java.util.List;

/** 好友列表查询结果。 */
public class RespFriendInfo {

    public interface Code {
        String NOT_AUTHOR = "000003";
        String NOT_SUPPORT = "000002";
        String SUCCESS = "000001";
    }

    private String code;
    private List<FriendInfo> data;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<FriendInfo> getData() {
        return this.data;
    }

    public void setData(List<FriendInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RespFriendInfo{code='" + this.code + "', data=" + this.data + '}';
    }
}