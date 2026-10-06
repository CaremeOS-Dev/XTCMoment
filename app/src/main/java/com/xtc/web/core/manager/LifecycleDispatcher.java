package com.xtc.web.core.manager;

import android.content.Intent;
import android.os.Bundle;

import com.xtc.web.core.callback.LifecycleCallbacks;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/** 页面生命周期事件分发器，把宿主页面的生命周期广播给所有已注册的 Web 组件。 */
public class LifecycleDispatcher extends LifecycleCallbacks {

    private static LifecycleDispatcher instance;
    private Object value = new Object();
    private ConcurrentHashMap<LifecycleCallbacks, Object> lifecycleCallbacksSet = new ConcurrentHashMap<>();

    public static LifecycleDispatcher getInstance() {
        if (instance == null) {
            instance = new LifecycleDispatcher();
        }
        return instance;
    }

    private LifecycleDispatcher() {
    }

    public void registerCallback(LifecycleCallbacks callbacks) {
        this.lifecycleCallbacksSet.put(callbacks, this.value);
    }

    public void unRegisterCallback(LifecycleCallbacks callbacks) {
        this.lifecycleCallbacksSet.remove(callbacks);
    }

    @Override
    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        Iterator<LifecycleCallbacks> iterator = this.lifecycleCallbacksSet.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().dispatchActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void disPatchOnCreate(Bundle savedInstanceState) {
        Iterator<LifecycleCallbacks> iterator = this.lifecycleCallbacksSet.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().disPatchOnCreate(savedInstanceState);
        }
    }

    @Override
    public void disPatchOnResume() {
        Iterator<LifecycleCallbacks> iterator = this.lifecycleCallbacksSet.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().disPatchOnResume();
        }
    }

    @Override
    public void dispatchOnStart() {
        Iterator<LifecycleCallbacks> iterator = this.lifecycleCallbacksSet.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().dispatchOnStart();
        }
    }

    @Override
    public void dispatchOnStop() {
        Iterator<LifecycleCallbacks> iterator = this.lifecycleCallbacksSet.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().dispatchOnStop();
        }
    }

    @Override
    public void dispatchOnDestroy() {
        Iterator<LifecycleCallbacks> iterator = this.lifecycleCallbacksSet.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().dispatchOnDestroy();
        }
        this.lifecycleCallbacksSet.clear();
    }

    @Override
    public void dispatchOnNewIntent(Intent intent) {
        Iterator<LifecycleCallbacks> iterator = this.lifecycleCallbacksSet.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next().dispatchOnNewIntent(intent);
        }
    }
}