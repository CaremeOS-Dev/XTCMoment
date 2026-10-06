package com.xtc.web.core.callback;

import android.content.Intent;
import android.os.Bundle;

/** Web 容器生命周期回调基类，宿主页面把生命周期事件转发给 Web 组件。 */
public abstract class LifecycleCallbacks {

    public void disPatchOnCreate(Bundle savedInstanceState) {
    }

    public void disPatchOnResume() {
    }

    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void dispatchOnDestroy() {
    }

    public void dispatchOnNewIntent(Intent intent) {
    }

    public void dispatchOnStart() {
    }

    public void dispatchOnStop() {
    }
}