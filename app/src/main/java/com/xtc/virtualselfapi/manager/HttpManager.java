package com.xtc.virtualselfapi.manager;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.bean.db.DbDecorate;
import com.xtc.virtualselfapi.bean.net.req.BaseReqeust;
import com.xtc.virtualselfapi.bean.net.req.ReqFriendInfo;
import com.xtc.virtualselfapi.bean.net.req.ReqPosition;
import com.xtc.virtualselfapi.bean.net.req.RequestGetCostumeData;
import com.xtc.virtualselfapi.bean.net.req.RespCardInfo;
import com.xtc.virtualselfapi.bean.net.req.RespPostion;
import com.xtc.virtualselfapi.bean.net.req.RespResource;
import com.xtc.virtualselfapi.bean.net.req.RespVersion;
import com.xtc.virtualselfapi.bean.net.resp.RespCurrentCostumeInfo;
import com.xtc.virtualselfapi.bean.net.resp.RespFriendFormat;
import com.xtc.virtualselfapi.interfaces.IHttpService;

import java.util.List;

import rx.Observable;

/**
 * 虚拟形象网络请求管理器。
 */
public class HttpManager extends HttpServiceProxy {

    private static final String TAG = "Virtual_Self_Api_HttpManager";
    private static final String ERROR_URL = "//null";
    private static final String NORMAL_URL = "http://api.watch.okii.com/";

    private final IHttpService httpService;
    private final String openId;

    public HttpManager(Context context) {
        super(context);
        String gatewayUrl = BaseUrlManager.getGatewayUrl(context);
        gatewayUrl = (TextUtils.isEmpty(gatewayUrl) || gatewayUrl.contains(ERROR_URL)) ? NORMAL_URL : gatewayUrl;
        this.openId = OpenIdManager.get(context);
        this.httpService = this.httpClient.requestSync(gatewayUrl, IHttpService.class);
    }

    public Observable<RespVersion> getVersion() {
        LogUtil.d(TAG, "start get version!");
        return this.httpService.getVersion().map(new HttpRxJavaCallback());
    }

    public Observable<RespResource> getResource() {
        return this.httpService.getResource().map(new HttpRxJavaCallback());
    }

    public Observable<List<DbDecorate>> getCustomDecorateList() {
        LogUtil.d(TAG, "start get no support decorate ids!");
        return this.httpService.getCustomDecorateList().map(new HttpRxJavaCallback());
    }

    public Observable<RespPostion> getPosition(int suitId, List<Integer> ornamentIdList) {
        ReqPosition request = new ReqPosition();
        request.setSuitId(suitId);
        request.setOrnamentIdList(ornamentIdList);
        return this.httpService.getPosition(request).map(new HttpRxJavaCallback());
    }

    public Observable<List<RespFriendFormat>> getFriendList(List<String> friendOpenIdList) {
        ReqFriendInfo request = new ReqFriendInfo();
        request.setOpenId(this.openId);
        request.setfOpenIdList(friendOpenIdList);
        return this.httpService.getFriendList(request).map(new HttpRxJavaCallback());
    }

    public Observable<RespCurrentCostumeInfo> getCurrentCostume(String openId) {
        RequestGetCostumeData request = new RequestGetCostumeData();
        request.setOpenId(openId);
        return this.httpService.getCurrentCostumeNew(request).map(new HttpRxJavaCallback());
    }

    public Observable<RespCardInfo> getCardInfo() {
        BaseReqeust request = new BaseReqeust();
        request.setOpenId(this.openId);
        return this.httpService.getCardInfo(request).map(new HttpRxJavaCallback());
    }
}