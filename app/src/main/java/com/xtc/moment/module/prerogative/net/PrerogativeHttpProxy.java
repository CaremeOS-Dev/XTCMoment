package com.xtc.moment.module.prerogative.net;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.prerogative.bean.LoadPersonalReq;
import com.xtc.moment.module.prerogative.bean.PersonalResponse;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * 特权资源接口代理。
 */
public class PrerogativeHttpProxy extends HttpServiceProxy {

    private static final String TAG = "PrerogativeHttpProxy";

    public PrerogativeHttpProxy(Context context) {
        super(context);
    }

    public Observable<List<ResourceNetResponse>> getPrerogativeResource() {
        IPrerogativeHttp http = (IPrerogativeHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), IPrerogativeHttp.class);
        return http.getResource().map(new HttpRxJavaCallback())
                .onErrorReturn(new Func1<Throwable, List<ResourceNetResponse>>() {
                    @Override
                    public List<ResourceNetResponse> call(Throwable throwable) {
                        LogUtil.e(TAG, "getPrerogativeResource onError: ", throwable);
                        return null;
                    }
                });
    }

    public Observable<PersonalResponse> getPersonalData(String watchId) {
        IPrerogativeHttp http = (IPrerogativeHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), IPrerogativeHttp.class);
        return http.getPersonalData(new LoadPersonalReq(watchId)).map(new HttpRxJavaCallback());
    }
}