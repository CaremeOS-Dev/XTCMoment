package com.xtc.moment.share.other;

import android.content.Context;

import com.xtc.common.bigdata.BehaviorUtil;

import java.util.HashMap;

/**
 * 分享相关埋点。
 */
public class BehaviorEvent {

    private final Context appContext;

    public BehaviorEvent(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void shareEvent(String transaction, boolean singleChat, String receiver, String packageName, int scene,
            int contentType) {
        HashMap<String, String> params = new HashMap<>();
        params.put("transaction", transaction);
        params.put("receiver", receiver);
        params.put("packageName", packageName);
        params.put("scene", String.valueOf(scene));
        params.put("singleChat", String.valueOf(singleChat));
        params.put("contentType", String.valueOf(contentType));
        BehaviorUtil.customEvent(this.appContext, "launcher_share_content", params);
    }

    public static void publishLbs(Context context) {
        BehaviorUtil.customEvent(context, "moment_publish_lbs", null);
    }

    public static void lbsStarLevel(Context context, int level) {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(level));
        BehaviorUtil.customEvent(context, "moment_lbs_star_level", params);
    }

    public static void recordNewLikeEmpty(Context context) {
        BehaviorUtil.customEvent(context, "moment_new_like_empty", null);
    }

    public static void recordLocalSize(Context context, int size) {
        HashMap<String, String> params = new HashMap<>();
        params.put("size", String.valueOf(size));
        BehaviorUtil.customEvent(context, "moment_local_search_size", params);
    }
}