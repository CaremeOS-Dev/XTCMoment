package com.xtc.web.core.verify;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;

import java.util.List;

import rx.Observable;

/** 白名单拉取接口的 retrofit 代理。 */
public class VerifyServeHttpProxy extends HttpServiceProxy {

    private boolean initBaseUrl;
    private IVerifyHttp verifyHttp;

    public VerifyServeHttpProxy(Context context) {
        super(context);
    }

    /** 拉取服务端下发的 H5 白名单。 */
    public Observable<List<String>> getVerifyDatas(Context context) {
        return this.httpClient.request(BaseUrlManager.getGatewayUrl(context), IVerifyHttp.class)
                .getVerifyDatas()
                .map(new HttpRxJavaCallback());
    }
}