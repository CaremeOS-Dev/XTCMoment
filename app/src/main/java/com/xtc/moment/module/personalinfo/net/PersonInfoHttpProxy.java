package com.xtc.moment.module.personalinfo.net;

import android.content.Context;

import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.moment.MomentApp;
import com.xtc.moment.module.personalinfo.net.bean.BaseRequestBean;
import com.xtc.moment.module.personalinfo.net.bean.DeleteFriendParam;
import com.xtc.moment.module.personalinfo.net.bean.GetBadgeResponse;
import com.xtc.moment.module.personalinfo.net.bean.LikeRequest;
import com.xtc.moment.module.personalinfo.net.bean.PersonalGradeResultBean;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoAndLikeRuleRequest;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoAndLikeRuleResponse;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoResponse;
import com.xtc.moment.module.personalinfo.net.bean.RespPersonalInfoUnite;
import com.xtc.moment.module.personalinfo.net.interfaces.IPersonalInfoHttp;

import rx.Observable;

/**
 * 个人中心接口代理。
 */
public class PersonInfoHttpProxy extends HttpServiceProxy {

    public PersonInfoHttpProxy(Context context) {
        super(context);
    }

    public Observable<PersonalInfoResponse> getPersonInfo(String watchId) {
        IPersonalInfoHttp http = (IPersonalInfoHttp) this.httpClient
                .request(BaseUrlManager.getDefaultUrl(this.context), IPersonalInfoHttp.class);
        return http.getPersonalInfo(new BaseRequestBean(watchId)).map(new HttpRxJavaCallback());
    }

    public Observable<PersonalInfoAndLikeRuleResponse> getSignatureAndLikeRule(String watchId, int level) {
        IPersonalInfoHttp http = (IPersonalInfoHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), IPersonalInfoHttp.class);
        return http.getSignatureAndLikeRule(new PersonalInfoAndLikeRuleRequest(watchId, level))
                .map(new HttpRxJavaCallback());
    }

    public Observable<GetBadgeResponse> getBadge(String watchId) {
        IPersonalInfoHttp http = (IPersonalInfoHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), IPersonalInfoHttp.class);
        return http.getBadge(new BaseRequestBean(watchId)).map(new HttpRxJavaCallback());
    }

    public Observable<String> deleteFriend(String watchId, String friendId) {
        IPersonalInfoHttp http = (IPersonalInfoHttp) this.httpClient
                .request(BaseUrlManager.getChatUrl(this.context), IPersonalInfoHttp.class);
        return http.deleteFriend(new DeleteFriendParam(watchId, friendId)).map(new HttpRxJavaCallback());
    }

    public Observable<String> like(LikeRequest request) {
        IPersonalInfoHttp http = (IPersonalInfoHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), IPersonalInfoHttp.class);
        return http.like(request).map(new HttpRxJavaCallback());
    }

    public Observable<PersonalGradeResultBean> getPersonalGradeInfo(String watchId, String model) {
        IPersonalInfoHttp http = (IPersonalInfoHttp) this.httpClient
                .request(BaseUrlManager.getPointsUrl(this.context), IPersonalInfoHttp.class);
        return http.getPersonalGradeInfo(watchId, model).map(new HttpRxJavaCallback());
    }

    public Observable<RespPersonalInfoUnite> getPersonalInfoUnite(String watchId) {
        BaseRequestBean request = new BaseRequestBean(watchId);
        request.setExwatchId(MomentApp.getWatchId());
        IPersonalInfoHttp http = (IPersonalInfoHttp) this.httpClient
                .request(BaseUrlManager.getGatewayUrl(this.context), IPersonalInfoHttp.class);
        return http.getPersonalInfoUnite(request).map(new HttpRxJavaCallback());
    }
}