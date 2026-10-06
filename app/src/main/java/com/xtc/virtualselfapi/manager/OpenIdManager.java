package com.xtc.virtualselfapi.manager;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.system.account.WatchAccountBase;

/**
 * openId 缓存管理器。
 */
public class OpenIdManager {

    private static String openId;

    public static synchronized String get(Context context) {
        if (TextUtils.isEmpty(openId)) {
            openId = WatchAccountBase.getOpenID(context);
        }
        return openId;
    }
}