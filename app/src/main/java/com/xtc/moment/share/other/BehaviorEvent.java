package com.xtc.moment.share.other;

import android.content.Context;

import com.xtc.common.bigdata.BehaviorUtil;

import java.util.HashMap;

/**
 * 分享相关的行为埋点上报。
 */
public class BehaviorEvent {

    private static final String EVENT_SHARE_CONTENT = "launcher_share_content";
    private static final String EVENT_PUBLISH_LBS = "moment_publish_lbs";
    private static final String EVENT_LBS_STAR_LEVEL = "moment_lbs_star_level";
    private static final String EVENT_NEW_LIKE_EMPTY = "moment_new_like_empty";
    private static final String EVENT_LOCAL_SEARCH_SIZE = "moment_local_search_size";

    private final Context appContext;

    public BehaviorEvent(Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * 上报一次分享内容事件。
     *
     * @param transaction 分享事务号
     * @param singleChat  是否为单聊场景
     * @param receiver    接收方标识
     * @param packageName 来源应用包名
     * @param scene       分享场景
     * @param contentType 内容类型
     */
    public void shareEvent(String transaction, boolean singleChat, String receiver, String packageName, int scene,
            int contentType) {
        HashMap<String, String> params = new HashMap<>();
        params.put("transaction", transaction);
        params.put("receiver", receiver);
        params.put("packageName", packageName);
        params.put("scene", String.valueOf(scene));
        params.put("singleChat", String.valueOf(singleChat));
        params.put("contentType", String.valueOf(contentType));
        BehaviorUtil.customEvent(this.appContext, EVENT_SHARE_CONTENT, params);
    }

    /** 上报发布定位事件。 */
    public static void publishLbs(Context context) {
        BehaviorUtil.customEvent(context, EVENT_PUBLISH_LBS, null);
    }

    /** 上报定位星级事件。 */
    public static void lbsStarLevel(Context context, int starLevel) {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(starLevel));
        BehaviorUtil.customEvent(context, EVENT_LBS_STAR_LEVEL, params);
    }

    /** 上报点赞列表为空的事件。 */
    public static void recordNewLikeEmpty(Context context) {
        BehaviorUtil.customEvent(context, EVENT_NEW_LIKE_EMPTY, null);
    }

    /** 上报本地搜索到的动态数量。 */
    public static void recordLocalSize(Context context, int size) {
        HashMap<String, String> params = new HashMap<>();
        params.put("size", String.valueOf(size));
        BehaviorUtil.customEvent(context, EVENT_LOCAL_SEARCH_SIZE, params);
    }
}