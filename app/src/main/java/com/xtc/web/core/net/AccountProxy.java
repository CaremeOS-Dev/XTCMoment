package com.xtc.web.core.net;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.web.core.data.req.ReqAccount;
import com.xtc.web.core.data.resp.RespAccountInfo;

import rx.Observable;

/** 账号接口的 retrofit 代理。 */
public class AccountProxy extends HttpServiceProxy {

    private IAccountInfo iAccountInfo;

    public AccountProxy(Context context) {
        super(context);
        String gatewayUrl = BaseUrlManager.getGatewayUrl(context);
        this.iAccountInfo = this.httpClient.request(gatewayUrl, IAccountInfo.class);
        this.httpClient.requestSync(gatewayUrl, IAccountInfo.class);
    }

    /** 请求 openId。 */
    public Observable<RespAccountInfo> getOpenIdRequest(ReqAccount reqAccount) {
        return this.iAccountInfo.getOpenIdRequest(reqAccount).map(new HttpRxJavaCallback());
    }
}