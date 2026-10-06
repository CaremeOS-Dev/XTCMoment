package com.xtc.web.client.utils;

import android.content.Context;

import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.utils.system.WatchModelUtil;

import java.util.HashMap;

/** web.client 的行为埋点。 */
public class BehaviorEvent {

    /** 上报 WebView 缓存清理事件。 */
    public static void deleteWebViewCache(Context context, long limitSpace, long deleteSpace) {
        HashMap<String, String> extend = new HashMap<>();
        extend.put("deleteSpace", String.valueOf(deleteSpace));
        extend.put("limitSpace", String.valueOf(limitSpace));
        extend.put("model", WatchModelUtil.getWatchInnerModel());
        BehaviorUtil.customEvent(context, "webview_delete_cache", extend);
    }
}