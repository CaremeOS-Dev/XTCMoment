package com.xtc.qiniu.net;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.qiniu.bean.NetDownloadTokenParam;

import java.util.List;

import rx.Observable;

/** Retrofit proxy of the cloud-storage token endpoints. */
public class ICloudHttpServiceProxy extends HttpServiceProxy {

    public ICloudHttpServiceProxy(Context context) {
        super(context);
    }

    public Observable<String> getUploadToken(int spaceType, String key) {
        return service().getUploadToken(spaceType, key).map(new HttpRxJavaCallback<String>());
    }

    public Observable<String> getUploadToken(int spaceType) {
        return service().getUploadToken(spaceType).map(new HttpRxJavaCallback<String>());
    }

    public Observable<List<String>> getDownloadToken(NetDownloadTokenParam param) {
        return service().getDownloadToken(param).map(new HttpRxJavaCallback<List<String>>());
    }

    private ICloudHttpService service() {
        return this.httpClient.request(BaseUrlManager.getDefaultUrl(this.context), ICloudHttpService.class);
    }
}