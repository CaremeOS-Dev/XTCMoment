package com.xtc.im.core.common.listener;

import com.xtc.im.core.common.request.PushRequest;
import com.xtc.im.core.common.response.PushResponse;

/** 收到推送响应回调。 */
public interface OnReceiveListener {
    void onReceive(PushRequest pushRequest, PushResponse pushResponse);
}