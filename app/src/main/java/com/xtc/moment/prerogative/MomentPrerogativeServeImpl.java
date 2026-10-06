package com.xtc.moment.prerogative;

import android.content.Context;

import com.xtc.database.ormlite.CollectionUtil;
import com.xtc.log.LogUtil;
import com.xtc.moment.MomentApp;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeBackground;
import com.xtc.moment.db.bean.prerogative.DbMomentPrerogativeLike;
import com.xtc.moment.module.prerogative.bean.EmotionsEntity;
import com.xtc.moment.module.prerogative.bean.PersonalResponse;
import com.xtc.moment.module.prerogative.bean.ResourceNetResponse;
import com.xtc.moment.module.prerogative.net.PrerogativeHttpProxy;
import com.xtc.moment.prerogative.handler.BackgroundPrerogativeHandler;
import com.xtc.moment.prerogative.handler.LikePrerogativeHandler;
import com.xtc.moment.util.SharedTool;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 特权服务实现：拉取资源包、解压入库，并维护用户已购特权与当前使用状态。
 */
public class MomentPrerogativeServeImpl implements IPrerogativeServe {

    private static volatile MomentPrerogativeServeImpl INSTANTS = null;
    private static final String TAG = "MomentPrerogativeServeImpl";
    private static final long REFRESH_TIME = TimeUnit.HOURS.toMillis(3);

    public static boolean isNeedRefreshPersonalPrerogative = true;

    private BackgroundPrerogativeHandler mBackgroundPrerogativeHandler;
    private final Context mContext;
    private LikePrerogativeHandler mLikePrerogativeHandler;
    private final PrerogativeHttpProxy prerogativeHttpProxy;

    private MomentPrerogativeServeImpl(Context context) {
        this.mContext = context.getApplicationContext();
        this.prerogativeHttpProxy = new PrerogativeHttpProxy(this.mContext);
        this.mLikePrerogativeHandler = new LikePrerogativeHandler(this.mContext);
        this.mBackgroundPrerogativeHandler = new BackgroundPrerogativeHandler(this.mContext);
    }

    public static IPrerogativeServe getInstance(Context context) {
        if (INSTANTS == null) {
            synchronized (MomentPrerogativeServeImpl.class) {
                if (INSTANTS == null) {
                    INSTANTS = new MomentPrerogativeServeImpl(context);
                }
            }
        }
        return INSTANTS;
    }

    @Override
    public void initPrerogativeResource(final InitPrerogativeCallback callback) {
        initResource().flatMap(new Func1<Boolean, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(Boolean value) {
                if (MomentPrerogativeServeImpl.isNeedRefreshPersonalPrerogative || MomentPrerogativeServeImpl.this.hasOverdueData()) {
                    MomentPrerogativeServeImpl.isNeedRefreshPersonalPrerogative = false;
                    return MomentPrerogativeServeImpl.this.getPersonalPrerogativeData();
                }
                return Observable.just(value);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(MomentPrerogativeServeImpl.TAG, "initPrerogativeResource : ", throwable);
                MomentPrerogativeServeImpl.isNeedRefreshPersonalPrerogative = true;
                if (callback != null) {
                    callback.initFail();
                }
            }

            @Override
            public void onNext(Boolean value) {
                LogUtil.d(MomentPrerogativeServeImpl.TAG, "initPrerogativeResource complete: b = [" + value + "]");
                if (callback != null) {
                    callback.initSuccess();
                }
            }
        });
    }

    private boolean hasOverdueData() {
        return this.mBackgroundPrerogativeHandler.hasOverdueData() || this.mLikePrerogativeHandler.hasOverdueData();
    }

    private Observable<Boolean> initResource() {
        LogUtil.d(TAG, "initResource");
        return Observable.just(false).flatMap(new Func1<Boolean, Observable<List<ResourceNetResponse>>>() {
            @Override
            public Observable<List<ResourceNetResponse>> call(Boolean value) {
                if (System.currentTimeMillis() - SharedTool.getPrerogativeRefreshTime(MomentPrerogativeServeImpl.this.mContext) >= MomentPrerogativeServeImpl.REFRESH_TIME) {
                    return MomentPrerogativeServeImpl.this.prerogativeHttpProxy.getPrerogativeResource();
                }
                return Observable.just((List<ResourceNetResponse>) null);
            }
        }).map(new Func1<List<ResourceNetResponse>, Boolean>() {
            @Override
            public Boolean call(List<ResourceNetResponse> resources) {
                boolean allSuccess = true;
                MomentPrerogativeServeImpl.this.mLikePrerogativeHandler.getAllLocalData(true);
                MomentPrerogativeServeImpl.this.mBackgroundPrerogativeHandler.getAllLocalData(true);
                if (CollectionUtil.isEmpty(resources)) {
                    return false;
                }
                for (int i = 0; i < resources.size(); i++) {
                    ResourceNetResponse resource = resources.get(i);
                    if (resource != null) {
                        boolean success;
                        if (resource.getId() == 2) {
                            success = MomentPrerogativeServeImpl.this.mLikePrerogativeHandler.initPrerogativeResource(resource).booleanValue();
                        } else if (resource.getId() == 3) {
                            success = MomentPrerogativeServeImpl.this.mBackgroundPrerogativeHandler.initPrerogativeResource(resource).booleanValue();
                        } else {
                            continue;
                        }
                        allSuccess = success & allSuccess;
                    }
                }
                if (allSuccess) {
                    SharedTool.savePrerogativeRefreshTime(MomentPrerogativeServeImpl.this.mContext, System.currentTimeMillis());
                }
                return Boolean.valueOf(allSuccess);
            }
        });
    }

    public Observable<Boolean> getPersonalPrerogativeData() {
        return this.prerogativeHttpProxy.getPersonalData(MomentApp.getWatchId()).map(new Func1<PersonalResponse, Boolean>() {
            @Override
            public Boolean call(PersonalResponse response) {
                if (response == null || response.getEmotions() == null) {
                    return false;
                }
                List<EmotionsEntity> emotions = response.getEmotions();
                HashMap<Integer, EmotionsEntity> likeMap = new HashMap<>();
                HashMap<Integer, EmotionsEntity> backgroundMap = new HashMap<>();
                for (EmotionsEntity emotion : emotions) {
                    if (emotion != null) {
                        if (emotion.isMomentLikeEquity()) {
                            likeMap.put(Integer.valueOf(emotion.getPrerogativeId()), emotion);
                        } else if (emotion.isMomentBgEquity()) {
                            backgroundMap.put(Integer.valueOf(emotion.getPrerogativeId()), emotion);
                        }
                    }
                }
                MomentPrerogativeServeImpl.this.mBackgroundPrerogativeHandler.refreshLocalPrerogativeData(backgroundMap);
                MomentPrerogativeServeImpl.this.mLikePrerogativeHandler.refreshLocalPrerogativeData(likeMap);
                return true;
            }
        });
    }

    @Override
    public int getCurrentUseLikeEmotionId() {
        return this.mLikePrerogativeHandler.getCurrentUseEmotionId();
    }

    @Override
    public int getCurrentUseBackgroundEmotionId() {
        return this.mBackgroundPrerogativeHandler.getCurrentUseEmotionId();
    }

    @Override
    public DbMomentPrerogativeLike getPrerogativeLikeByEmotionId(int emotionId) {
        return this.mLikePrerogativeHandler.getPrerogativeByEmotionId(emotionId);
    }

    @Override
    public DbMomentPrerogativeBackground getPrerogativeBackgroundByEmotionId(int emotionId) {
        BackgroundPrerogativeHandler handler = this.mBackgroundPrerogativeHandler;
        if (handler == null) {
            return null;
        }
        return handler.getPrerogativeByEmotionId(emotionId);
    }
}