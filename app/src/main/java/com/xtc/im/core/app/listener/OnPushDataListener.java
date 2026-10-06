package com.xtc.im.core.app.listener;

import com.xtc.im.core.app.bean.PushMessage;

/** 收到推送消息回调。 */
public interface OnPushDataListener {
    void onPushData(PushMessage pushMessage);
}