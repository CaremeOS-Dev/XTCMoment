package com.xtc.web.client.net;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.web.client.db.DbWebUrl;

import java.util.List;

import rx.Observable;

/** H5 url 列表接口的 retrofit 代理。 */
public class WebUrlProxy extends HttpServiceProxy {

    private IWebUrl iWebUrl;
    private String packageName;

    public WebUrlProxy(Context context) {
        super(context);
        this.packageName = context.getPackageName();
        String defaultUrl = BaseUrlManager.getDefaultUrl(context);
        this.iWebUrl = this.httpClient.request(defaultUrl, IWebUrl.class);
        this.httpClient.requestSync(defaultUrl, IWebUrl.class);
    }

    /** 拉取当前应用的 H5 入口 url 列表。 */
    public Observable<List<DbWebUrl>> getWebUrlList() {
        return this.iWebUrl.getWebUrlList(this.packageName).map(new HttpRxJavaCallback());
    }
}