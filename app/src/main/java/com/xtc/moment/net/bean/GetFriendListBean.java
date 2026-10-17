package com.xtc.moment.net.bean;

/**
 * 自建服务端 {@code /extra/getFriendList} 的入参。
 *
 * <p>本机通讯录 provider 拿不到好友时客户端回退到这个接口；请求体只需要
 * 账号自身的 watchId，服务端据此返回该账号的好友列表。</p>
 */
public class GetFriendListBean {

    private String watchId;

    public String getWatchId() {
        return this.watchId;
    }

    public void setWatchId(String watchId) {
        this.watchId = watchId;
    }
}
