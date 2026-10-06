package com.xtc.web.client.manager;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.web.client.data.request.ReqJsBehavior;

/** H5 行为埋点转发。 */
public class JsBehaviorUtils {

    /** 有扩展字段时按自定义事件上报，否则按点击事件上报。 */
    public static void behavior(Context context, ReqJsBehavior reqJsBehavior) {
        if (TextUtils.isEmpty(reqJsBehavior.getEvent())) {
            return;
        }
        if (reqJsBehavior.getObj() == null) {
            BehaviorUtil.clickEvent(context, reqJsBehavior.getEvent());
        } else {
            BehaviorUtil.customEvent(context, reqJsBehavior.getEvent(), reqJsBehavior.getObj());
        }
    }
}