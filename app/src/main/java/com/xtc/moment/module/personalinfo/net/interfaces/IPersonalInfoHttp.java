package com.xtc.moment.module.personalinfo.net.interfaces;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.module.personalinfo.net.bean.BaseRequestBean;
import com.xtc.moment.module.personalinfo.net.bean.DeleteFriendParam;
import com.xtc.moment.module.personalinfo.net.bean.GetBadgeResponse;
import com.xtc.moment.module.personalinfo.net.bean.LikeRequest;
import com.xtc.moment.module.personalinfo.net.bean.PersonalGradeResultBean;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoAndLikeRuleRequest;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoAndLikeRuleResponse;
import com.xtc.moment.module.personalinfo.net.bean.PersonalInfoResponse;
import com.xtc.moment.module.personalinfo.net.bean.RespPersonalInfoUnite;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * 个人中心相关接口。
 */
public interface IPersonalInfoHttp {

    @POST("/smartwatch/geniusAccount/watchGetInfo")
    Observable<NetBaseResult<PersonalInfoResponse>> getPersonalInfo(@Body BaseRequestBean request);

    @POST("/social-service/personalInfo/getPersonalInfo")
    Observable<NetBaseResult<PersonalInfoAndLikeRuleResponse>> getSignatureAndLikeRule(
            @Body PersonalInfoAndLikeRuleRequest request);

    @POST("/social-service/medal/simple")
    Observable<NetBaseResult<GetBadgeResponse>> getBadge(@Body BaseRequestBean request);

    @POST("/watchfriend/delFriendByWatch")
    Observable<NetBaseResult<String>> deleteFriend(@Body DeleteFriendParam param);

    @POST("/social-service/personalInfo/like")
    Observable<NetBaseResult<String>> like(@Body LikeRequest request);

    @GET("/operation/exp/getWatchScoreLevelInfo/{watchId}/{model}")
    Observable<NetBaseResult<PersonalGradeResultBean>> getPersonalGradeInfo(@Path("watchId") String watchId,
            @Path("model") String model);

    @POST("/social-service/personalInfo/getPersonalInfoUnite")
    Observable<NetBaseResult<RespPersonalInfoUnite>> getPersonalInfoUnite(@Body BaseRequestBean request);
}