package com.xtc.moment.net;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.httplib.bean.NetBaseResult;
import com.xtc.httplib.net.BaseUrlManager;
import com.xtc.httplib.net.HttpRxJavaCallback;
import com.xtc.httplib.net.HttpServiceProxy;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.bean.DbMoment;
import com.xtc.moment.db.bean.DbMomentComment;
import com.xtc.moment.module.bean.FriendsVisibleBean;
import com.xtc.moment.module.bean.FriendsVisibleBeanReq;
import com.xtc.moment.module.bean.PoiBean;
import com.xtc.moment.module.bean.VideoTokenParam;
import com.xtc.moment.module.report.bean.ReportDataBean;
import com.xtc.moment.module.report.bean.ReportInformParam;
import com.xtc.moment.module.report.bean.StartReportRequest;
import com.xtc.moment.net.bean.BanStateBean;
import com.xtc.moment.net.bean.BanStateBody;
import com.xtc.moment.net.bean.CommentBean;
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
import com.xtc.moment.net.bean.OfficialCommentResultBean;
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
import com.xtc.moment.prerogative.MomentPrerogativeServeImpl;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.tasks.domain.usecase.PublishMomentTask;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.utils.encode.JSONUtil;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * 好友圈网络请求代理：组装请求体、切换 baseUrl，并把响应解包成业务数据。
 */
public class MomentHttpServiceProxy extends HttpServiceProxy {

    private static final int SEARCH_PERMISSION_TYPE_GET = 1;
    private static final int SEARCH_PERMISSION_TYPE_NO = 0;
    private static final String TAG = "XTC_MOMENT_MomentHttpServiceProxy";

    private boolean initBaseUrl;
    private Context mContext;
    private MomentHttp momentHttp;

    public MomentHttpServiceProxy(Context context) {
        super(context);
        this.mContext = context;
        ServerCache.putHttpService(this);
        checkBaseUrl();
    }

    public void checkBaseUrl() {
        if (!this.initBaseUrl || this.momentHttp == null) {
            String momentUrl = BaseUrlManager.getMomentUrl(this.context);
            if (TextUtils.isEmpty(momentUrl)) {
                this.initBaseUrl = false;
                momentUrl = BaseUrlManager.getDefaultUrl(this.context);
            } else {
                this.initBaseUrl = true;
            }
            this.momentHttp = this.httpClient.request(momentUrl, MomentHttp.class);
        }
    }

    public void setInitBaseUrl(boolean initBaseUrl) {
        this.initBaseUrl = initBaseUrl;
    }

    public Observable<Moment> publishMoment(String watchId, String resource, int resourceId, String content, int type, String packageName, FriendsVisibleBean friendsVisibleBean) {
        checkBaseUrl();
        PublishMomentBody body = new PublishMomentBody();
        body.setWatchId(watchId);
        body.setResource(resource);
        body.setResourceId(resourceId);
        body.setContent(content);
        body.setType(type);
        body.setPackageName(packageName);
        body.setEmotionId(MomentPrerogativeServeImpl.getInstance(this.mContext).getCurrentUseBackgroundEmotionId());
        if (friendsVisibleBean != null) {
            body.setPermissionType(friendsVisibleBean.getType());
            body.setLookupIds(friendsVisibleBean.getFriends());
        }
        return this.momentHttp.publishMoment(body).map(new HttpRxJavaCallback<Moment>());
    }

    public Observable<Moment> publishMoment(String watchId, String resource, int resourceId, String content, int type, String packageName, PoiBean poiBean, FriendsVisibleBean friendsVisibleBean) {
        checkBaseUrl();
        PublishMomentBody body = new PublishMomentBody();
        body.setWatchId(watchId);
        body.setResource(resource);
        body.setResourceId(resourceId);
        body.setContent(content);
        body.setType(type);
        body.setPackageName(packageName);
        body.setEmotionId(MomentPrerogativeServeImpl.getInstance(this.mContext).getCurrentUseBackgroundEmotionId());
        if (friendsVisibleBean != null) {
            body.setPermissionType(friendsVisibleBean.getType());
            body.setLookupIds(friendsVisibleBean.getFriends());
        }
        if (poiBean != null) {
            body.setLatitude(Double.parseDouble(poiBean.getLocation().getLatitude()));
            body.setLongitude(Double.parseDouble(poiBean.getLocation().getLongitude()));
            body.setLocation(poiBean.getCity() + poiBean.getAddressDesc());
            body.setLocationType(poiBean.getLocationType());
            if (poiBean.getDetail_info() != null && !TextUtils.isEmpty(poiBean.getDetail_info().getTag())) {
                body.setLocationTag(poiBean.getDetail_info().getTag());
                LogUtil.i(TAG, "locationTag = " + poiBean.getDetail_info().getTag());
            }
        }
        return this.momentHttp.publishMoment(body).map(new HttpRxJavaCallback<Moment>());
    }

    public Observable<Moment> publishMoment(PublishMomentTask.RequestValues requestValues) {
        checkBaseUrl();
        return this.momentHttp.publishMoment(requestValues).map(new HttpRxJavaCallback<Moment>());
    }

    public Observable<Moment> publishMoment(String watchId, String resource, int resourceId, String content, int type, String packageName, List<Integer> typeList) {
        return publishMoment(watchId, resource, resourceId, content, type, packageName, typeList, null);
    }

    public Observable<Moment> publishMoment(String watchId, String resource, int resourceId, String content, int type, String packageName, List<Integer> typeList, PoiBean poiBean) {
        checkBaseUrl();
        PublishMomentBody body = new PublishMomentBody();
        body.setWatchId(watchId);
        body.setResource(resource);
        body.setResourceId(resourceId);
        body.setContent(content);
        body.setType(type);
        body.setPackageName(packageName);
        body.setTypeList(JSONUtil.toJSON(typeList));
        body.setEmotionId(MomentPrerogativeServeImpl.getInstance(this.mContext).getCurrentUseBackgroundEmotionId());
        if (poiBean != null) {
            body.setLatitude(Double.parseDouble(poiBean.getLocation().getLatitude()));
            body.setLongitude(Double.parseDouble(poiBean.getLocation().getLongitude()));
            body.setLocation(poiBean.getCity() + poiBean.getAddressDesc());
            body.setLocationType(poiBean.getLocationType());
        }
        return this.momentHttp.publishMoment(body).map(new HttpRxJavaCallback<Moment>());
    }

    public Observable<DefaultResponse> praiseMoment(String momentId, String momentWatchId, String watchId) {
        checkBaseUrl();
        PraiseMomentBody body = new PraiseMomentBody();
        body.setMomentId(momentId);
        body.setMomentWatchId(momentWatchId);
        body.setWatchId(watchId);
        body.setEmotionId(MomentPrerogativeServeImpl.getInstance(this.mContext).getCurrentUseLikeEmotionId());
        return this.momentHttp.praiseMoment(body).map(new HttpRxJavaCallback<DefaultResponse>());
    }

    public Observable<String> cancelPraiseMoment(String momentId, String momentWatchId, String watchId) {
        checkBaseUrl();
        PraiseMomentBody body = new PraiseMomentBody();
        body.setMomentId(momentId);
        body.setMomentWatchId(momentWatchId);
        body.setWatchId(watchId);
        return this.momentHttp.cancelPraiseMoment(body).map(new HttpRxJavaCallback<String>());
    }

    public Observable<DefaultResponse> praiseAdvertise(String momentId, String momentWatchId, String watchId) {
        return createLocalPraise(momentId, momentWatchId, watchId);
    }

    /**
     * 官方动态点赞不请求服务端，直接构造一个本地成功响应。
     */
    private Observable<DefaultResponse> createLocalPraise(String momentId, String momentWatchId, String watchId) {
        final DefaultResponse response = new DefaultResponse();
        response.setCreateTime(System.currentTimeMillis());
        response.setMomentId(momentId);
        response.setMomentWatchId(momentWatchId);
        response.setWatchId(watchId);
        return Observable.create(new Observable.OnSubscribe<DefaultResponse>() {
            @Override
            public void call(Subscriber<? super DefaultResponse> subscriber) {
                subscriber.onNext(response);
                subscriber.onCompleted();
            }
        }).map(new Func1<DefaultResponse, DefaultResponse>() {
            @Override
            public DefaultResponse call(DefaultResponse value) {
                return value;
            }
        });
    }

    public Observable<PraiseResponse> getPraiseRecord(List<String> momentIds, String momentWatchId) {
        checkBaseUrl();
        GetAllPraiseBody body = new GetAllPraiseBody();
        body.setMomentIds(momentIds);
        body.setMomentWatchId(momentWatchId);
        return this.momentHttp.getPraiseRecord(body).map(new HttpRxJavaCallback<PraiseResponse>());
    }

    public Observable<Moments> searchMoment(long begin, long end, int friend, long from, long size, long lastLikeTime, String watchId, String currentWatchId) {
        checkBaseUrl();
        SearchMomentBody body = new SearchMomentBody();
        body.setBegin(begin);
        body.setEnd(end);
        body.setFriend(friend);
        body.setFrom(from);
        body.setLastLikeTime(lastLikeTime);
        body.setSize(size);
        body.setWatchId(watchId);
        body.setCurrentWatchId(currentWatchId);
        body.setCommentPageSize(5);
        if (ModuleSwitchUtil.queryModuleSwitchByBoolean(this.mContext, ModuleSwitchConstant.MODULE_VISIBLE_RANGE, false)) {
            body.setSearchPermission(SEARCH_PERMISSION_TYPE_GET);
        } else {
            body.setSearchPermission(SEARCH_PERMISSION_TYPE_NO);
        }
        return this.momentHttp.searchMoment(body).map(new HttpRxJavaCallback<Moments>());
    }

    public Observable<List<OfficialCommentResultBean>> searchOfficialComment(String lookUpId, List<String> advertIdList) {
        checkBaseUrl();
        OfficialCommentRequestBean request = new OfficialCommentRequestBean();
        request.setAdvertIdList(advertIdList);
        request.setLookUpId(lookUpId);
        return this.momentHttp.searchOfficialComment(request).map(new Func1<NetBaseResult<OfficialCommentResult>, List<OfficialCommentResultBean>>() {
            @Override
            public List<OfficialCommentResultBean> call(NetBaseResult<OfficialCommentResult> result) {
                if ("000001".equals(result.getCode())) {
                    return result.getData().getAdvertVoList();
                }
                return null;
            }
        });
    }

    public Observable<TemplateResponseBean> getMomentTemplate(long from, long size, long updateId) {
        checkBaseUrl();
        TemplateRequestBean request = new TemplateRequestBean();
        request.setFrom(from);
        request.setSize(size);
        request.setUpdateId(updateId);
        return this.momentHttp.getMomentTemplate(request).map(new HttpRxJavaCallback<TemplateResponseBean>());
    }

    public Observable<DeleteResultBean> deleteMoment(String watchId, DbMoment moment) {
        checkBaseUrl();
        DeleteMomentBean body = new DeleteMomentBean();
        body.setWatchId(watchId);
        body.setMomentId(moment.getMomentId());
        LogUtil.d(TAG, "deleteMoment#requestBody:" + body);
        return this.momentHttp.deleteMoment(body).map(new HttpRxJavaCallback<DeleteResultBean>());
    }

    public Observable<CommentResultBean> commentMoment(DbMomentComment comment) {
        checkBaseUrl();
        MomentCommentBean body = new MomentCommentBean();
        body.setMomentId(comment.getMomentId());
        body.setComment(comment.getComment());
        body.setMomentWatchId(comment.getMomentWatchId());
        body.setWatchId(comment.getWatchId());
        if (2 == comment.getType()) {
            body.setReplyId(comment.getReplyId());
        }
        LogUtil.d(TAG, "commentMoment#requestBody:" + body);
        return this.momentHttp.commentMoment(body).map(new HttpRxJavaCallback<CommentResultBean>());
    }

    public Observable<CommentOfficialResultBean> commentAdvertMoment(DbMomentComment comment) {
        checkBaseUrl();
        MomentCommentOfficialBean body = new MomentCommentOfficialBean();
        body.setAdvertId(comment.getMomentId());
        body.setComment(comment.getComment());
        body.setParentWatchId(comment.getWatchId());
        body.setWatchId(comment.getWatchId());
        if (2 == comment.getType()) {
            body.setReplyWatchId(comment.getReplyId());
            body.setReplyCommentId(comment.getCommentId());
            body.setParentWatchId(comment.getParentWatchId());
        }
        LogUtil.d(TAG, "commentAdvertMoment#requestBody:" + body);
        return this.momentHttp.commentOfficialMoment(body).map(new HttpRxJavaCallback<CommentOfficialResultBean>());
    }

    private Observable<CommentResultBean> createLocalComment(MomentCommentBean body) {
        final CommentResultBean result = new CommentResultBean();
        result.setResult("1");
        CommentBean comment = new CommentBean();
        comment.setComment(body.getComment());
        comment.setCommentId("" + System.currentTimeMillis() + body.getWatchId());
        comment.setCreateTime(System.currentTimeMillis());
        comment.setMomentId(body.getMomentId());
        comment.setWatchId(body.getWatchId());
        Context context = this.mContext;
        comment.setWatchName(context != null ? context.getResources().getString(R.string.me) : "");
        comment.setReplyId(body.getReplyId());
        if (!TextUtils.isEmpty(body.getReplyId()) && body.getReplyId().equals(body.getWatchId())) {
            Context ctx = this.mContext;
            comment.setReplyName(ctx != null ? ctx.getResources().getString(R.string.me) : "");
        }
        result.setComment(comment);
        return Observable.create(new Observable.OnSubscribe<CommentResultBean>() {
            @Override
            public void call(Subscriber<? super CommentResultBean> subscriber) {
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).map(new Func1<CommentResultBean, CommentResultBean>() {
            @Override
            public CommentResultBean call(CommentResultBean value) {
                return value;
            }
        });
    }

    public Observable<BanStateBean> updateBanState(String bindNumber) {
        BanStateBody body = new BanStateBody();
        body.setBindNumber(bindNumber);
        return this.momentHttp.updateBanState(body).map(new HttpRxJavaCallback<BanStateBean>());
    }

    public Observable<NormalResultBean> deleteServerComment(DbMomentComment comment) {
        checkBaseUrl();
        DeleteCommentBean body = new DeleteCommentBean();
        body.setCommentId(comment.getCommentId());
        body.setMomentId(comment.getMomentId());
        body.setMomentWatchId(comment.getMomentWatchId());
        body.setWatchId(comment.getWatchId());
        if (!TextUtils.isEmpty(body.getMomentWatchId()) && body.getMomentWatchId().length() < 40) {
            DeleteOfficialCommentBean officialBody = new DeleteOfficialCommentBean();
            officialBody.setAdvertId(comment.getMomentId());
            officialBody.setCommentId(comment.getCommentId());
            officialBody.setParentWatchId(comment.getWatchId());
            officialBody.setWatchId(comment.getWatchId());
            if (comment.getType() == 2) {
                officialBody.setParentWatchId(comment.getReplyId());
            }
            LogUtil.d(TAG, "deleteOfficialComment#requestBody:" + officialBody);
            return this.momentHttp.deleteOfficialComment(officialBody);
        }
        LogUtil.d(TAG, "deleteComment#requestBody:" + body);
        return this.momentHttp.deleteComment(body);
    }

    public Observable<NetBaseResult<String>> launchedReport(StartReportRequest request) {
        return this.httpClient.request(BaseUrlManager.getGatewayUrl(this.context), MomentHttp.class).launchedReport(request);
    }

    public Observable<NetBaseResult<ReportDataBean>> queryReportInform(ReportInformParam param) {
        return this.httpClient.request(BaseUrlManager.getGatewayUrl(this.context), MomentHttp.class).queryReportInform(param);
    }

    public Observable<VideoTokenVoResponse> getUploadVideoToken(VideoTokenParam param) {
        return this.httpClient.request(BaseUrlManager.getMomentUrl(this.context), MomentHttp.class).getUploadVideoToken(param).map(new HttpRxJavaCallback<VideoTokenVoResponse>());
    }

    public Observable<SearchCommentResponse> searchComment(int pageNum, int pageSize, String momentId, String momentWatchId, boolean flag) {
        checkBaseUrl();
        SearchCommentBody body = new SearchCommentBody();
        body.setMomentId(momentId);
        body.setMomentWatchId(momentWatchId);
        body.setWatchId(AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext));
        body.setPageNum(pageNum);
        body.setPageSize(pageSize);
        return this.httpClient.request(BaseUrlManager.getMomentUrl(this.context), MomentHttp.class).searchComment(body).map(new HttpRxJavaCallback<SearchCommentResponse>());
    }

    public Observable<NetBaseResult<SearchOfficialCommentResponse>> searchOfficialComment(int pageNum, int pageSize, String advertId, String lookUpId, boolean flag) {
        checkBaseUrl();
        SearchOfficialCommentBody body = new SearchOfficialCommentBody();
        body.setAdvertId(advertId);
        body.setLookUpId(AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext));
        body.setPageNum(pageNum);
        body.setPageSize(pageSize);
        return this.httpClient.request(BaseUrlManager.getMomentUrl(this.context), MomentHttp.class).searchOfficialCommentPage(body);
    }

    public Observable<NetBaseResult<SearchAllCommentResponse>> searchAllComment(String momentId, String momentWatchId) {
        SearchAllCommentRequest request = new SearchAllCommentRequest();
        request.setMomentId(momentId);
        request.setMomentWatchId(momentWatchId);
        request.setWatchId(AccountInfoServerImpl.getInstance(this.mContext).getWatchAccountInfo().getWatchId(this.mContext));
        return this.httpClient.request(BaseUrlManager.getMomentUrl(this.context), MomentHttp.class).searchAllComment(request);
    }

    public Observable<NormalResultBean> sendGift(SendGiftRequest request) {
        return this.momentHttp.sendGift(request);
    }

    public Observable<SearchGiftResponse> searchGift(SearchGiftRequest request) {
        return this.momentHttp.searchGift(request).map(new HttpRxJavaCallback<SearchGiftResponse>());
    }

    public Observable<String> reportMoment(ReportMomentReq request) {
        return this.momentHttp.reportMoment(request).map(new HttpRxJavaCallback<String>());
    }

    public Observable<String> doLbsStar(LbsStarBean body) {
        return this.momentHttp.doLbsStar(body).map(new HttpRxJavaCallback<String>());
    }

    public Observable<List<FriendsVisibleBean>> getLookUpFriends(FriendsVisibleBeanReq request) {
        return this.momentHttp.permissionSearch(request).map(new HttpRxJavaCallback<List<FriendsVisibleBean>>());
    }

    public Observable<String> permissionUpdate(FriendsVisibleBean bean) {
        return this.momentHttp.permissionUpdate(bean).map(new HttpRxJavaCallback<String>());
    }

    public Observable<ReminderConfig> getReminderConfig() {
        return this.httpClient.request(BaseUrlManager.getGatewayUrl(this.context), MomentHttp.class).getReminderConfig().map(new HttpRxJavaCallback<ReminderConfig>());
    }
}