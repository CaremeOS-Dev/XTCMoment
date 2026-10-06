package com.xtc.web.client.manager;

import android.content.Context;

import com.xtc.log.LogUtil;
import com.xtc.web.client.data.response.RespEruptData;
import com.xtc.web.client.receiver.CommonReceiver;
import com.xtc.web.core.XtcWebView;

/** 系统事件（来电、闹钟、低电量等）触发 H5 事件的管理器。 */
public class JsEruptManager {

    private static final String TAG = "WebClient_JsEruptManager";
    private static JsEruptManager instance;
    private CommonReceiver commonReceiver;
    private boolean isRegistered = false;
    private Context mContext;
    private XtcWebView xtcWebView;

    public static synchronized JsEruptManager getInstance(Context context) {
        if (instance == null) {
            instance = new JsEruptManager(context.getApplicationContext());
        }
        return instance;
    }

    private JsEruptManager(Context context) {
        this.mContext = context;
    }

    public synchronized void setXtcWebView(XtcWebView xtcWebView) {
        this.xtcWebView = xtcWebView;
    }

    public synchronized void release() {
        this.xtcWebView = null;
        if (this.isRegistered) {
            this.isRegistered = false;
            if (this.commonReceiver != null) {
                this.mContext.unregisterReceiver(this.commonReceiver);
            }
        }
        this.mContext = null;
        instance = null;
    }

    /** H5 注册系统事件监听。 */
    public void registerReceiver() {
        if (this.xtcWebView == null) {
            LogUtil.d(TAG, "registerReceiver: but xtcWebView = null");
            return;
        }
        if (this.commonReceiver == null) {
            this.commonReceiver = new CommonReceiver();
        }
        this.mContext.registerReceiver(this.commonReceiver, CommonReceiver.getCameraCommonFilter());
        this.isRegistered = true;
    }

    /** 把系统事件类型回调给 H5。 */
    public synchronized void eruptData(int type) {
        LogUtil.d(TAG, "receive push type = " + type);
        if (this.xtcWebView != null && this.isRegistered) {
            RespEruptData respEruptData = new RespEruptData();
            respEruptData.setType(type);
            LogUtil.d(TAG, "call eruptData = " + respEruptData);
            this.xtcWebView.callHandler("eruptData", new Object[]{respEruptData});
        }
    }
}