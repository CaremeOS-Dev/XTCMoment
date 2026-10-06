package com.xtc.moment.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.xtc.log.LogUtil;
import com.xtc.moment.db.bean.TaskDoneEvent;
import com.xtc.moment.util.HandlerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class KeepAliveService extends Service {

    private static final String TAG = "keep_pre_KeepAliveService";

    private Notification notification;
    private volatile int taskIndex = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "onCreate , this = " + hashCode());
        notification = new Notification.Builder(this).build();
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().register(KeepAliveService.this);
            }
        });
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        try {
            startKeep();
        } catch (Exception e) {
            LogUtil.e(TAG, "startKeep error " + e);
        }
    }

    private void startKeep() {
        startForeground(22, notification);
        taskIndex++;
        LogUtil.d(TAG, "onStart , new taskIndex = " + taskIndex);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskDoneEvent(TaskDoneEvent taskDoneEvent) {
        try {
            checkToStopKeep(taskDoneEvent);
        } catch (Exception e) {
            LogUtil.e(TAG, "checkToStopKeep error = " + e);
        }
    }

    private void checkToStopKeep(TaskDoneEvent taskDoneEvent) {
        taskIndex--;
        LogUtil.d(TAG, "one task done --> " + taskDoneEvent + " , now taskIndex = " + taskIndex);
        if (taskIndex <= 0) {
            taskIndex = 0;
            prepareToStop();
        }
    }

    private void prepareToStop() {
        LogUtil.i(TAG, "all task done , prepare to stop !");
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                stopForeground(true);
                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notification = null;
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().unregister(KeepAliveService.this);
            }
        });
        LogUtil.d(TAG, "onDestroy done , this = " + hashCode());
    }
}