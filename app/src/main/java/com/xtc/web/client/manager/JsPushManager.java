package com.xtc.web.client.manager;

import com.xtc.log.LogUtil;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.data.response.RespPushData;
import com.xtc.web.core.XtcWebView;

import java.util.ArrayList;
import java.util.List;

/** 向 H5 推送数据的管理器，仅推送 H5 已注册的类型。 */
public class JsPushManager {

    private static final String TAG = Constants.TAG + JsPushManager.class.getSimpleName();
    private static JsPushManager instance;
    private List<Integer> registerType;
    private XtcWebView xtcWebView;

    public static synchronized JsPushManager getInstance() {
        if (instance == null) {
            instance = new JsPushManager();
        }
        return instance;
    }

    private JsPushManager() {
    }

    /** H5 注册需要接收的推送类型。 */
    public synchronized void registerType(List<Integer> typeList) {
        if (this.registerType == null) {
            this.registerType = new ArrayList<>();
        }
        this.registerType.addAll(typeList);
    }

    public synchronized void setXtcWebView(XtcWebView xtcWebView) {
        this.xtcWebView = xtcWebView;
        if (this.registerType != null) {
            this.registerType.clear();
        }
    }

    public synchronized void release() {
        this.xtcWebView = null;
    }

    /** 推送数据给 H5。 */
    public synchronized void pushData(int type, String content) {
        LogUtil.d(TAG, "receive push type = " + type);
        if (this.registerType != null && this.registerType.contains(Integer.valueOf(type)) && this.xtcWebView != null) {
            RespPushData respPushData = new RespPushData();
            respPushData.setType(type);
            respPushData.setContent(content);
            LogUtil.d(TAG, "call pushData = " + respPushData);
            this.xtcWebView.callHandler("pushData", new Object[]{respPushData});
        }
    }
}