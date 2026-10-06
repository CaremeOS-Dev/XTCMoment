package com.xtc.moment.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.StringConstant;
import com.xtc.moment.push.bean.ImMessage;
import com.xtc.moment.push.bean.ImMessageData;
import com.xtc.moment.serve.MomentTemplateServeImpl;
import com.xtc.moment.service.IMMomentService;
import com.xtc.moment.util.KeepAliveUtil;
import com.xtc.moment.util.SharedTool;
import com.xtc.utils.encode.JSONUtil;

public class MomentIMReceiver extends BroadcastReceiver {

    private static final String ACTION = "com.xtc.moment.im.data";
    private static final String TAG = "XTC_MOMENT_MomentIMReceiver";

    String CHANGE_LANGUAGE_ACTION = "com.xtc.launcher.ACTIOON_CHANGE_LANGUAGE";
    String CHANGE_REGION_ACTION = "com.xtc.launcher.ACTION_CHANGE_REGION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (ACTION.equals(action)) {
            ImMessageData messageData = JSONUtil.fromJSON(intent.getStringExtra("data"), ImMessageData.class);
            if (messageData == null) {
                LogUtil.e(TAG, "imMessageData is null");
                return;
            }
            ImMessage message = messageData.getMessage();
            if (message == null || message.getType() == null) {
                LogUtil.e("moment", "imMessage is null or type is null");
                return;
            }
            int type = message.getType().intValue();
            String content = message.getContent();
            LogUtil.d(TAG, "imMessage type = " + type + " , content = " + content);
            KeepAliveUtil.startKeep(context, "MomentIMReceiver-work");
            Intent serviceIntent = new Intent(context, (Class<?>) IMMomentService.class);
            serviceIntent.setAction(StringConstant.MOMENT_IM_ACTION);
            serviceIntent.putExtra(StringConstant.MOMENT_PUSH_TYPE, type);
            serviceIntent.putExtra(StringConstant.MOMENT_PUSH_MSG, content);
            context.startService(serviceIntent);
            return;
        }
        if (CHANGE_LANGUAGE_ACTION.equals(action) || CHANGE_REGION_ACTION.equals(action)) {
            SharedTool.removeReportInfoMessage(context);
            LogUtil.d(TAG, "监听到切换语言！！ delete size : " + MomentTemplateServeImpl.getInstance(context).deleteAllTemplates()
                    + "; saveTime " + SharedTool.saveCanRequestReportTime(context, System.currentTimeMillis()));
        }
    }
}