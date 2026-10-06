package com.xtc.moment.util;

import android.content.Context;
import android.content.Intent;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.module.bean.ShareAppMoment;
import com.xtc.moment.module.bean.ShareAppPublish;
import com.xtc.utils.encode.JSONUtil;

/**
 * 打开分享应用动态对应的第三方应用。
 */
public class ShareAppStartUtil {

    private static final String TAG = "ShareAppStartUtil";

    public static void startApp(Context context, DbMoment moment) {
        ShareAppPublish publish = convertToShareAppPublish(
                (ShareAppMoment) JSONUtil.fromJSON(moment.getContent(), ShareAppMoment.class));
        if (publish == null) {
            LogUtil.d(TAG, "share app publish is null!");
            return;
        }
        Intent intent = new Intent();
        intent.setAction("com.xtc.share.start.app");
        intent.putExtra("com.xtc.share.start.app.type", 2);
        intent.putExtra("com.xtc.share.start.app.info", JSONUtil.toJSON(publish));
        context.sendBroadcast(intent);
    }

    private static ShareAppPublish convertToShareAppPublish(ShareAppMoment moment) {
        if (moment == null) {
            LogUtil.d(TAG, "share app moment is null!");
            return null;
        }
        ShareAppPublish publish = new ShareAppPublish();
        publish.setAction(moment.getAction());
        publish.setAppName(moment.getAppName());
        publish.setDesc(moment.getDesc());
        publish.setExtInfo(moment.getExtInfo());
        publish.setTargetClass(moment.getTargetClass());
        publish.setTargetPackage(moment.getTargetPackage());
        publish.setTransaction(moment.getTransaction());
        publish.setPackageName(moment.getPackageName());
        return publish;
    }
}