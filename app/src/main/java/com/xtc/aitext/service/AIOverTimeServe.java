package com.xtc.aitext.service;

import com.xtc.log.LogUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * AI 创作超时检测服务。
 */
public class AIOverTimeServe {

    private static final String TAG = "ai_text_AIOverTimeServe";

    private Subscription subscription;
    private final AIOverTimeListener overTimeListener;
    private long overTimeSeconds;

    /** 超时监听。 */
    public interface AIOverTimeListener {
        void onOverTime();
    }

    public AIOverTimeServe(AIOverTimeListener overTimeListener) {
        this.overTimeListener = overTimeListener;
    }

    public void setOverTimeSeconds(long overTimeSeconds) {
        this.overTimeSeconds = overTimeSeconds;
    }

    /** 开始超时检测。 */
    public void start() {
        LogUtil.i(TAG, "startOverTimeDetection");
        this.subscription = Observable.timer(this.overTimeSeconds, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }

                    @Override
                    public void onNext(Long value) {
                        if (overTimeListener != null) {
                            overTimeListener.onOverTime();
                        } else {
                            cancel();
                        }
                    }
                });
    }

    /** 取消超时检测。 */
    public void cancel() {
        Subscription subscription = this.subscription;
        if (subscription == null || subscription.isUnsubscribed()) {
            return;
        }
        LogUtil.i(TAG, "cancelOverTimeDetection");
        this.subscription.unsubscribe();
    }
}