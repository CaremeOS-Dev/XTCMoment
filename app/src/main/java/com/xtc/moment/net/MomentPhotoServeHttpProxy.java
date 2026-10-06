package com.xtc.moment.net;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.moment.module.bean.PhotoTokenParam;
import com.xtc.moment.module.bean.PhotoTokenVo;
import com.xtc.moment.serve.bean.DownloadUrlVo;
import com.xtc.moment.serve.bean.FileBatchUrlParam;
import com.xtc.moment.serve.bean.FileUrlParam;

import java.util.List;

import rx.Observable;

/**
 * 动态图片上传/下载接口代理，按需切换 base url。
 */
public class MomentPhotoServeHttpProxy extends HttpServiceProxy {

    private boolean initBaseUrl;
    private IMomentPhotoHttp momentHttp;

    public MomentPhotoServeHttpProxy(Context context) {
        super(context);
    }

    private void checkBaseUrl() {
        if (this.initBaseUrl) {
            return;
        }
        String momentUrl = BaseUrlManager.getMomentUrl(this.context);
        if (TextUtils.isEmpty(momentUrl)) {
            this.initBaseUrl = false;
            momentUrl = BaseUrlManager.getDefaultUrl(this.context);
        } else {
            this.initBaseUrl = true;
        }
        this.momentHttp = (IMomentPhotoHttp) this.httpClient.request(momentUrl, IMomentPhotoHttp.class);
    }

    public Observable<PhotoTokenVo> getUploadToken(PhotoTokenParam param) {
        checkBaseUrl();
        return this.momentHttp.getUploadToken(param).map(new HttpRxJavaCallback());
    }

    public Observable<List<PhotoTokenVo>> getUploadTokens(List<PhotoTokenParam> paramList) {
        checkBaseUrl();
        return this.momentHttp.getUploadTokens(paramList).map(new HttpRxJavaCallback());
    }

    public Observable<DownloadUrlVo> getDownloadUrl(FileUrlParam param) {
        checkBaseUrl();
        return this.momentHttp.getDownloadUrl(param).map(new HttpRxJavaCallback());
    }

    public Observable<DownloadUrlVo> getDownloadBatchUrl(FileBatchUrlParam param) {
        checkBaseUrl();
        return this.momentHttp.getDownloadBatchUrl(param).map(new HttpRxJavaCallback());
    }
}