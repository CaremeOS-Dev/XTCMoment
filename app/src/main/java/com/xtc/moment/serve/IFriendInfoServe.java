package com.xtc.moment.serve;

import com.xtc.moment.module.bean.Friend;

import java.util.List;

/**
 * 好友信息服务接口。
 */
public interface IFriendInfoServe {
    List<Friend> getAllFriendInfo();

    /** One friend from the cached server/provider list, or {@code null}. */
    Friend getFriendByWatchId(String watchId);

    String getDefaultHead();

    void onContactChange();
}
