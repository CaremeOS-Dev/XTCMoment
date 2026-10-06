package com.xtc.moment.net;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.moment.MomentApp;
import com.xtc.moment.module.personalinfo.net.bean.BaseRequestBean;
import com.xtc.moment.net.bean.CommunityConversationReq;
import com.xtc.moment.net.bean.CommunityConversationResponse;
import com.xtc.moment.net.bean.CommunityDetailResponse;

import java.util.List;

import rx.Observable;

/**
 * 社区对话接口代理。
 */
public class CommunityConversationProxy extends HttpServiceProxy {

    public CommunityConversationProxy(Context context) {
        super(context);
    }

    public Observable<CommunityConversationResponse> getConventionHome() {
        ICommunityConversationHttp http = (ICommunityConversationHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), ICommunityConversationHttp.class);
        return http.getConventionHome(new BaseRequestBean(MomentApp.getWatchId()))
                .map(new HttpRxJavaCallback());
    }

    public Observable<List<CommunityDetailResponse>> getConventionContent(int chapterIndex) {
        ICommunityConversationHttp http = (ICommunityConversationHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), ICommunityConversationHttp.class);
        CommunityConversationReq request = new CommunityConversationReq(MomentApp.getWatchId());
        request.setChapterIndex(chapterIndex);
        return http.getConventionContent(request).map(new HttpRxJavaCallback());
    }
}