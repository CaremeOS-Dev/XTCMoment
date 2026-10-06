package com.xtc.moment.net;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.FriendsVisibleBeanReq;
import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.bean.ReportInformParam;
import com.xtc.moment.module.report.bean.StartReportRequest;
import com.xtc.moment.net.bean.BanStateBean;
import com.xtc.moment.net.bean.BanStateBody;
import com.xtc.moment.net.bean.CommentOfficialResultBean;
import com.xtc.moment.net.bean.CommentResultBean;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.net.bean.DeleteCommentBean;
import com.xtc.moment.net.bean.DeleteMomentBean;
import com.xtc.moment.net.bean.DeleteOfficialCommentBean;
import com.xtc.moment.net.bean.DeleteResultBean;
import com.xtc.moment.net.bean.GetAllPraiseBody;
import com.xtc.moment.net.bean.LbsStarBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.MomentCommentBean;
import com.xtc.moment.net.bean.MomentCommentOfficialBean;
import com.xtc.moment.net.bean.Moments;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.net.bean.OfficialCommentRequestBean;
import com.xtc.moment.net.bean.OfficialCommentResult;
import com.xtc.moment.net.bean.PraiseMomentBody;
import com.xtc.moment.net.bean.PraiseResponse;
import com.xtc.moment.net.bean.PublishMomentBody;
import com.xtc.moment.net.bean.ReminderConfig;
import com.xtc.moment.net.bean.ReportMomentReq;
import com.xtc.moment.net.bean.SearchAllCommentRequest;
import com.xtc.moment.net.bean.SearchAllCommentResponse;
import com.xtc.moment.net.bean.SearchCommentBody;
import com.xtc.moment.net.bean.SearchCommentResponse;
import com.xtc.moment.net.bean.SearchGiftRequest;
import com.xtc.moment.net.bean.SearchGiftResponse;
import com.xtc.moment.net.bean.SearchMomentBody;
import com.xtc.moment.net.bean.SearchOfficialCommentBody;
import com.xtc.moment.net.bean.SearchOfficialCommentResponse;
import com.xtc.moment.net.bean.SendGiftRequest;
import com.xtc.moment.net.bean.TemplateRequestBean;
import com.xtc.moment.net.bean.TemplateResponseBean;
import com.xtc.moment.net.bean.VideoTokenVoResponse;
import com.xtc.moment.tasks.domain.usecase.PublishMomentTask;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 好友圈主服务接口定义。
 */
public interface MomentHttp {

    @POST("/moment/public")
    Observable<NetBaseResult<Moment>> publishMoment(@Body PublishMomentBody body);

    @POST("/moment/public")
    Observable<NetBaseResult<Moment>> publishMoment(@Body PublishMomentTask.RequestValues requestValues);

    @POST("/moment/delete")
    Observable<NetBaseResult<DeleteResultBean>> deleteMoment(@Body DeleteMomentBean body);

    @POST("/moment/search")
    Observable<NetBaseResult<Moments>> searchMoment(@Body SearchMomentBody body);

    @POST("/moment/refreshos")
    Observable<NetBaseResult<String>> reportMoment(@Body ReportMomentReq request);

    @POST("/moment/isBannedToPost")
    Observable<NetBaseResult<BanStateBean>> updateBanState(@Body BanStateBody body);

    @POST("/moment/like")
    Observable<NetBaseResult<DefaultResponse>> praiseMoment(@Body PraiseMomentBody body);

    @POST("/moment/like/cancel")
    Observable<NetBaseResult<String>> cancelPraiseMoment(@Body PraiseMomentBody body);

    @POST("/moment/like/search")
    Observable<NetBaseResult<PraiseResponse>> getPraiseRecord(@Body GetAllPraiseBody body);

    @POST("/moment/comment")
    Observable<NetBaseResult<CommentResultBean>> commentMoment(@Body MomentCommentBean body);

    @POST("/moment/deleteComment")
    Observable<NormalResultBean> deleteComment(@Body DeleteCommentBean body);

    @POST("/moment/getMomentComment")
    Observable<NetBaseResult<SearchCommentResponse>> searchComment(@Body SearchCommentBody body);

    @POST("/moment/getMomentCommentAll")
    Observable<NetBaseResult<SearchAllCommentResponse>> searchAllComment(@Body SearchAllCommentRequest request);

    @POST("/moment/advertComment/comment")
    Observable<NetBaseResult<CommentOfficialResultBean>> commentOfficialMoment(@Body MomentCommentOfficialBean body);

    @POST("/moment/advertComment/deleteComment")
    Observable<NormalResultBean> deleteOfficialComment(@Body DeleteOfficialCommentBean body);

    @POST("/moment/advertComment/getAllAdvert")
    Observable<NetBaseResult<OfficialCommentResult>> searchOfficialComment(@Body OfficialCommentRequestBean request);

    @POST("/moment/advertComment/getAdvertComment")
    Observable<NetBaseResult<SearchOfficialCommentResponse>> searchOfficialCommentPage(@Body SearchOfficialCommentBody body);

    @POST("/moment/permission/search")
    Observable<NetBaseResult<List<FriendsVisibleBean>>> permissionSearch(@Body FriendsVisibleBeanReq request);

    @POST("/moment/permission/update")
    Observable<NetBaseResult<String>> permissionUpdate(@Body FriendsVisibleBean bean);

    @POST("/moment/doLbsStar")
    Observable<NetBaseResult<String>> doLbsStar(@Body LbsStarBean body);

    @POST("/moment/template")
    Observable<NetBaseResult<TemplateResponseBean>> getMomentTemplate(@Body TemplateRequestBean request);

    @POST("/moment/file/video/transfer")
    Observable<NetBaseResult<VideoTokenVoResponse>> getUploadVideoToken(@Body VideoTokenParam param);

    @POST("/moment/vlog/gift/search")
    Observable<NetBaseResult<SearchGiftResponse>> searchGift(@Body SearchGiftRequest request);

    @POST("/moment/vlog/gift/sendgift")
    Observable<NormalResultBean> sendGift(@Body SendGiftRequest request);

    @GET("/social-service/warmReminder/getWarmReminderConfig")
    Observable<NetBaseResult<ReminderConfig>> getReminderConfig();

    @POST("/social-service/inform/submit")
    Observable<NetBaseResult<String>> launchedReport(@Body StartReportRequest request);

    @POST("/social-service/inform/state")
    Observable<NetBaseResult<ReportDataBean>> queryReportInform(@Body ReportInformParam param);
}