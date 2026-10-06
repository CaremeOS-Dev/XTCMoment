package com.xtc.moment.share.model;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.moment.share.model.bean.NetAppShare;

import rx.Observable;

/**
 * 应用分享配置查询。
 */
public class NetManager extends HttpServiceProxy {

    private static final int MAX_TIMES = 1000;

    private final INetInterface netInterface;

    public NetManager(Context context) {
        super(context);
        this.netInterface = (INetInterface) this.httpClient
                .requestSync(BaseUrlManager.getDefaultUrl(context), INetInterface.class);
    }

    public Observable<NetAppShare> queryAppInfo(String packageName) {
        NetAppShare appShare = new NetAppShare();
        appShare.setPackageName(packageName);
        appShare.setAppKey("appkey");
        appShare.setAllow(1);
        appShare.setDeadline(0L);
        appShare.setMaxTimes(MAX_TIMES);
        return Observable.just(appShare);
    }
}