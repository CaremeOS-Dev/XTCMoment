package com.xtc.moment.data.remote;

import android.content.Context;

import com.xtc.moment.LogTag;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.CommentOfficialResultBean;
import com.xtc.moment.net.bean.CommentResultBean;
import com.xtc.moment.net.bean.LbsStarBean;
import com.xtc.moment.net.bean.Moment;
import com.xtc.moment.net.bean.NormalResultBean;
import com.xtc.moment.net.bean.ReminderConfig;
import com.xtc.moment.net.bean.ReportMomentReq;
import com.xtc.moment.net.bean.SearchGiftRequest;
import com.xtc.moment.net.bean.SearchGiftResponse;
import com.xtc.moment.net.bean.SendGiftRequest;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.IAccountInfoServe;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.tasks.domain.usecase.PublishCommentTask;
import com.xtc.moment.tasks.domain.usecase.PublishMomentTask;
import com.xtc.moment.tasks.domain.usecase.PublishOfficialCommentTask;
import com.xtc.system.account.Device;
import com.xtc.system.account.WatchDevice;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/**
 * 动态远端数据源：统一转发到 {@link MomentHttpServiceProxy}。
 */
public class MomentsRemoteDataSource {

    private static volatile MomentsRemoteDataSource INSTANCE;
    private static final String TAG = LogTag.tag("MomentsRemoteDataSource");

    private IAccountInfoServe iAccountInfoServe;
    private Device mDevice;
    private MomentHttpServiceProxy momentHttpServiceProxy;

    private MomentsRemoteDataSource(Context context) {
        this.mDevice = new WatchDevice(context);
        this.momentHttpServiceProxy = ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
        this.iAccountInfoServe = AccountInfoServerImpl.getInstance(context);
    }

    public static MomentsRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MomentsRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MomentsRemoteDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public Observable<Moment> publishMoment(PublishMomentTask.RequestValues requestValues) {
        return this.momentHttpServiceProxy.publishMoment(requestValues).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    public Observable<CommentResultBean> publishComment(PublishCommentTask.RequestValues requestValues) {
        return this.momentHttpServiceProxy.commentMoment(requestValues.getDbMomentComment()).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    public Observable<CommentOfficialResultBean> publishOfficialComment(PublishOfficialCommentTask.RequestValues requestValues) {
        return this.momentHttpServiceProxy.commentAdvertMoment(requestValues.getDbMomentComment()).throttleFirst(1000L, TimeUnit.MILLISECONDS);
    }

    public Observable<NormalResultBean> sendGift(SendGiftRequest request) {
        return this.momentHttpServiceProxy.sendGift(request);
    }

    public Observable<SearchGiftResponse> searchGift(SearchGiftRequest request) {
        return this.momentHttpServiceProxy.searchGift(request);
    }

    public Observable<String> reportMoment(ReportMomentReq request) {
        return this.momentHttpServiceProxy.reportMoment(request);
    }

    public Observable<String> doLbsStar(LbsStarBean body) {
        return this.momentHttpServiceProxy.doLbsStar(body);
    }

    public Observable<ReminderConfig> getReminderConfig() {
        return this.momentHttpServiceProxy.getReminderConfig();
    }
}