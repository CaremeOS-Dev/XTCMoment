package com.xtc.moment.net;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.module.personalinfo.net.bean.BaseRequestBean;
import com.xtc.moment.net.bean.CommunityConversationReq;
import com.xtc.moment.net.bean.CommunityConversationResponse;
import com.xtc.moment.net.bean.CommunityDetailResponse;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 社区对话（活动会场）相关接口。
 */
public interface ICommunityConversationHttp {

    @POST("/social-service/convention/home")
    Observable<NetBaseResult<CommunityConversationResponse>> getConventionHome(@Body BaseRequestBean requestBean);

    @POST("/social-service/convention/content")
    Observable<NetBaseResult<List<CommunityDetailResponse>>> getConventionContent(@Body CommunityConversationReq request);
}