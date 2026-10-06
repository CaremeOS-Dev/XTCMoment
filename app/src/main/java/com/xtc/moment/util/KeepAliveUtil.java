package com.xtc.moment.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.TaskDoneEvent;
import com.xtc.moment.service.KeepAliveService;

import org.greenrobot.eventbus.EventBus;

/**
 * 发布保活辅助：启动保活服务并在超时后派发任务完成事件。
 */
public class KeepAliveUtil {

    private static final String TAG = "keep_pre_KeepAliveUtil";

    private static final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message message) {
            super.dispatchMessage(message);
            LogUtil.d(TAG, "dispatchMessage ---");
            if (message != null && message.obj != null) {
                stopKeep(null, (String) message.obj);
                return;
            }
            LogUtil.e(TAG, "error , msg = " + message);
        }
    };

    public static void startKeep(Context context, String tag) {
        LogUtil.i(TAG, "startKeep , tag = " + tag);
        Context applicationContext = context.getApplicationContext();
        applicationContext.startService(new Intent(applicationContext, KeepAliveService.class));
        Message message = handler.obtainMessage();
        message.obj = tag;
        handler.sendMessageDelayed(message, 10000L);
    }

    private static void stopKeep(Context context, String tag) {
        LogUtil.i(TAG, "stopKeep , tag = " + tag);
        EventBus.getDefault().post(new TaskDoneEvent(tag));
    }
}