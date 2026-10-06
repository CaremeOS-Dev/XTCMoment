package com.xtc.moment.module.illegal.net;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.moment.module.illegal.net.bean.request.BannerNetBean;
import com.xtc.moment.module.illegal.net.bean.request.HighRiskRequestBean;
import com.xtc.moment.module.illegal.net.bean.request.InitViolationBean;
import com.xtc.moment.module.illegal.net.bean.request.ReportDisableBean;
import com.xtc.moment.module.illegal.net.bean.response.ViolationInfoBean;
import com.xtc.moment.module.illegal.net.interfaces.IIllegalHttp;

import rx.Observable;

/**
 * 违规/敏感内容相关的网络请求代理。
 */
public class IllegalHttpProxy extends HttpServiceProxy {

    public IllegalHttpProxy(Context context) {
        super(context);
    }

    public Observable<ViolationInfoBean> initViolationInfo(InitViolationBean request) {
        return ((IIllegalHttp) this.httpClient.request(BaseUrlManager.getGatewayUrl(this.context),
                IIllegalHttp.class)).initViolationInfo(request).map(new HttpRxJavaCallback());
    }

    public Observable<String> uploadDisableCount(ReportDisableBean request) {
        return ((IIllegalHttp) this.httpClient.request(BaseUrlManager.getGatewayUrl(this.context),
                IIllegalHttp.class)).uploadDisableCount(request).map(new HttpRxJavaCallback());
    }

    public Observable<BannerNetBean> getBannerContent(InitViolationBean request) {
        return ((IIllegalHttp) this.httpClient.request(BaseUrlManager.getGatewayUrl(this.context),
                IIllegalHttp.class)).getBannerContent(request).map(new HttpRxJavaCallback());
    }

    public Observable<BannerNetBean> getHighRiskBannerContent(HighRiskRequestBean request) {
        return ((IIllegalHttp) this.httpClient.request(BaseUrlManager.getGatewayUrl(this.context),
                IIllegalHttp.class)).getHighRiskBannerContent(request).map(new HttpRxJavaCallback());
    }
}