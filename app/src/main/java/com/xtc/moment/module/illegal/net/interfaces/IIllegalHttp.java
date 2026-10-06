package com.xtc.moment.module.illegal.net.interfaces;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.module.illegal.net.bean.request.BannerNetBean;
import com.xtc.moment.module.illegal.net.bean.request.HighRiskRequestBean;
import com.xtc.moment.module.illegal.net.bean.request.InitViolationBean;
import com.xtc.moment.module.illegal.net.bean.request.ReportDisableBean;
import com.xtc.moment.module.illegal.net.bean.request.ReportIllegalBean;
import com.xtc.moment.module.illegal.net.bean.response.ViolationInfoBean;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 违规处置相关接口。
 */
public interface IIllegalHttp {

    @POST("/social-service/violation/initViolationInfo")
    Observable<NetBaseResult<ViolationInfoBean>> initViolationInfo(@Body InitViolationBean request);

    @POST("/social-service/inform/content")
    Observable<NetBaseResult<BannerNetBean>> getBannerContent(@Body InitViolationBean request);

    @POST("/social-service/violation/searchMomentRiskRecord")
    Observable<NetBaseResult<BannerNetBean>> getHighRiskBannerContent(@Body HighRiskRequestBean request);

    @POST("/social-service/violation/uploadDelayCount")
    Observable<NetBaseResult<String>> uploadDelayCount(@Body ReportIllegalBean request);

    @POST("/social-service/violation/uploadBanRecord")
    Observable<NetBaseResult<String>> uploadDisableCount(@Body ReportDisableBean request);
}