package com.xtc.moment.data.remote;

import android.content.Context;

import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.net.bean.DefaultResponse;
import com.xtc.moment.serve.AccountInfoServerImpl;
import com.xtc.moment.serve.ServerCache;
import com.xtc.moment.tasks.domain.usecase.CancelPraiseMomentTask;
import com.xtc.moment.tasks.domain.usecase.PraiseMomentTask;

import rx.Observable;

/**
 * 点赞相关的远端数据源，转发到 {@link MomentHttpServiceProxy}。
 */
public class MomentLikeRemoteDataSource {

    private static volatile MomentLikeRemoteDataSource INSTANCE;
    private Context mContext;
    private MomentHttpServiceProxy momentHttpServiceProxy;
    private final String watchId;

    public MomentLikeRemoteDataSource(Context context) {
        this.mContext = context;
        this.momentHttpServiceProxy = ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
        this.watchId = AccountInfoServerImpl.getInstance(context).getWatchAccountInfo().getWatchId(context);
    }

    public static MomentLikeRemoteDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MomentLikeRemoteDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MomentLikeRemoteDataSource(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public Observable<DefaultResponse> praiseMoment(PraiseMomentTask.RequestValues requestValues) {
        return this.momentHttpServiceProxy.praiseMoment(requestValues.getMomentId(), requestValues.getMomentWatchId(), requestValues.getWatchId());
    }

    public Observable<String> cancelPraiseMoment(CancelPraiseMomentTask.RequestValues requestValues) {
        return this.momentHttpServiceProxy.cancelPraiseMoment(requestValues.getMomentId(), requestValues.getWatchId(), this.watchId);
    }

    public Observable<DefaultResponse> praiseAdvertise(PraiseMomentTask.RequestValues requestValues) {
        return this.momentHttpServiceProxy.praiseAdvertise(requestValues.getMomentId(), requestValues.getMomentWatchId(), requestValues.getWatchId());
    }
}