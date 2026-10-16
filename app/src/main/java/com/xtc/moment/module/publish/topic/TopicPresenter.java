package com.xtc.moment.module.publish.topic;

import android.content.Context;

import com.xtc.architecture.mvp.core.MvpPresenter;
import com.xtc.log.LogUtil;
import com.xtc.moment.net.bean.RespTopicConfig;
import com.xtc.moment.serve.IMomentTemplateServe;
import com.xtc.moment.serve.MomentTemplateServeImpl;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 「小括号」选择页 presenter：拉取推荐话题列表并回调视图。
 */
public class TopicPresenter extends MvpPresenter<ITopicView> {

    private static final String TAG = "XTC_MOMENT_TopicPresenter";

    private final Context context;
    private final IMomentTemplateServe momentTemplateServe;

    public TopicPresenter(Context context) {
        this.context = context;
        this.momentTemplateServe = MomentTemplateServeImpl.getInstance(context);
    }

    /**
     * 请求推荐话题配置。
     */
    public void getTopicConfigData() {
        this.momentTemplateServe.getTopicConfigData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<RespTopicConfig>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        LogUtil.e(TAG, "getTopicConfigData ErrorMessage =", throwable);
                        if (TopicPresenter.this.getView() != null) {
                            TopicPresenter.this.getView().showTopicError();
                        }
                    }

                    @Override
                    public void onNext(RespTopicConfig respTopicConfig) {
                        LogUtil.d(TAG, "getTopicConfigData data = " + respTopicConfig);
                        if (TopicPresenter.this.getView() != null) {
                            TopicPresenter.this.getView().showTopicList(respTopicConfig);
                        }
                    }
                });
    }
}

