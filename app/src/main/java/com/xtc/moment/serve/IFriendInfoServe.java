package com.xtc.moment.serve;

import com.xtc.moment.module.bean.Friend;

import java.util.List;

/**
 * 好友信息服务接口。
 */
public interface IFriendInfoServe {
    List<Friend> getAllFriendInfo();

    String getDefaultHead();

    void onContactChange();
}