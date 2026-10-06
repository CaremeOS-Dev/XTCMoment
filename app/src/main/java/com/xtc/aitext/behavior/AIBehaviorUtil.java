package com.xtc.aitext.behavior;

import com.xtc.aitext.manager.AITextManager;
import com.xtc.anim.alphaplayer.BuildConfig;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.common.bigdata.BehaviorUtil;

import java.util.HashMap;

/**
 * AI 文案埋点工具。
 */
public class AIBehaviorUtil {

    private static final String EVENT_ID = "ai_text_detail";
    private static final String KEY_FUN_TYPE = "ai_text_fun_type";
    private static final String KEY_FROM_SOURCE = "ai_text_from_source";
    private static final String KEY_STYLE_NAME = "ai_text_style_name";
    private static final String KEY_RESULT = "ai_text_result";
    private static final String KEY_SHARE_RECORD = "ai_text_share_record";
    private static final String KEY_POINT_METHOD = "ai_text_point_method";

    /** 进入 AI 文案主页。 */
    public static void reportEnterHome() {
        HashMap<String, String> params = new HashMap<>(2);
        params.put(KEY_FUN_TYPE, "1");
        params.put(KEY_FROM_SOURCE, String.valueOf(AITextManager.getInstance(ContextUtils.getContext()).getConfig().getClientType()));
        BehaviorUtil.customEvent(ContextUtils.getContext(), EVENT_ID, params);
    }

    /** 选择风格。 */
    public static void reportSelectStyle(String styleName) {
        HashMap<String, String> params = new HashMap<>(3);
        params.put(KEY_FUN_TYPE, "2");
        params.put(KEY_FROM_SOURCE, String.valueOf(AITextManager.getInstance(ContextUtils.getContext()).getConfig().getClientType()));
        params.put(KEY_STYLE_NAME, styleName);
        BehaviorUtil.customEvent(ContextUtils.getContext(), EVENT_ID, params);
    }

    /** 上报创作结果。 */
    public static void reportCreateResult(boolean success) {
        HashMap<String, String> params = new HashMap<>(3);
        params.put(KEY_FUN_TYPE, "3");
        params.put(KEY_FROM_SOURCE, String.valueOf(AITextManager.getInstance(ContextUtils.getContext()).getConfig().getClientType()));
        params.put(KEY_RESULT, String.valueOf(success));
        BehaviorUtil.customEvent(ContextUtils.getContext(), EVENT_ID, params);
    }

    /** 上报分享结果。 */
    public static void reportShareResult(boolean success) {
        HashMap<String, String> params = new HashMap<>(3);
        params.put(KEY_FUN_TYPE, "4");
        params.put(KEY_SHARE_RECORD, String.valueOf(success));
        params.put(KEY_FROM_SOURCE, String.valueOf(AITextManager.getInstance(ContextUtils.getContext()).getConfig().getClientType()));
        BehaviorUtil.customEvent(ContextUtils.getContext(), EVENT_ID, params);
    }

    /** 上报查看记录。 */
    public static void reportViewRecord() {
        HashMap<String, String> params = new HashMap<>(2);
        params.put(KEY_FUN_TYPE, BuildConfig.JenkinsRevision);
        params.put(KEY_FROM_SOURCE, String.valueOf(AITextManager.getInstance(ContextUtils.getContext()).getConfig().getClientType()));
        BehaviorUtil.customEvent(ContextUtils.getContext(), EVENT_ID, params);
    }

    /** 上报领取方式。 */
    public static void reportObtainMethod(String method) {
        HashMap<String, String> params = new HashMap<>(3);
        params.put(KEY_FUN_TYPE, "6");
        params.put(KEY_POINT_METHOD, method);
        params.put(KEY_FROM_SOURCE, String.valueOf(AITextManager.getInstance(ContextUtils.getContext()).getConfig().getClientType()));
        BehaviorUtil.customEvent(ContextUtils.getContext(), EVENT_ID, params);
    }
}