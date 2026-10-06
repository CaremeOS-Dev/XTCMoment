package com.xtc.moment.serve;

import android.content.Context;

import com.xtc.moment.serve.bean.WatchAccountInfo;

/**
 * 账号信息服务接口。
 */
public interface IAccountInfoServe {
    WatchAccountInfo getWatchAccountInfo();

    String getMyHeadIconPath();

    void initWatchAccountInfo();

    void initAllInfoDirectly();

    void refreshMyIconPath(Context context);

    boolean isInit();

    void resetToInit();
}