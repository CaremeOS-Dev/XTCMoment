package com.xtc.aitext.util;

import android.content.Context;

import com.xtc.aitext.bean.AIStyleTextBean;
import com.xtc.aitext.constant.Constant;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.SharedManager;

/**
 * AI 文案本地存储工具，保存首次进入、权限同意与上次使用风格。
 */
public class AITextShareUtil {

    /** 是否首次使用 AI 文案。 */
    public static boolean isAiFirst(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.SharedConstants.SHARE_AI_FIRST, true);
    }

    /** 标记已使用过 AI 文案。 */
    public static boolean setAiUsed(Context context) {
        return SharedManager.getInstance(context).putBoolean(Constant.SharedConstants.SHARE_AI_FIRST, false);
    }

    /** 是否已同意权限。 */
    public static boolean hasAgreePermission(Context context) {
        return SharedManager.getInstance(context).getBoolean(Constant.SharedConstants.SHARE_HAS_AGREE_PERMISSION, true);
    }

    /** 标记已同意权限。 */
    public static boolean setAgreePermission(Context context) {
        return SharedManager.getInstance(context).putBoolean(Constant.SharedConstants.SHARE_HAS_AGREE_PERMISSION, false);
    }

    /** 保存上次使用的风格。 */
    public static boolean saveLastStyle(Context context, AIStyleTextBean styleBean) {
        return SharedManager.getInstance(context).putString(Constant.SharedConstants.SHARE_LAST_STYLE, JSONUtil.toJSON(styleBean));
    }

    /** 读取上次使用的风格 JSON。 */
    public static String getLastStyleJson(Context context) {
        return SharedManager.getInstance(context).getString(Constant.SharedConstants.SHARE_LAST_STYLE, "");
    }
}