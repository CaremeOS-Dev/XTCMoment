package com.xtc.httplib.net;

import com.xtc.log.LogUtil;

import rx.Subscriber;

/** Subscriber that logs the request duration. */
public class BaseSubscriber<T> extends Subscriber<T> {
    protected long completeTime;
    protected long startTime;

    @Override
    public void onNext(T value) {
    }

    @Override
    public void onStart() {
        LogUtil.d("BaseSubscriber.onStart on thread:" + Thread.currentThread().getName());
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void onCompleted() {
        LogUtil.d("BaseSubscriber.onCompleted on thread:" + Thread.currentThread().getName());
        this.completeTime = System.currentTimeMillis();
        LogUtil.i("http spent time[" + (this.completeTime - this.startTime) + "]ms");
    }

    @Override
    public void onError(Throwable e) {
        LogUtil.e("BaseSubscriber.onError on thread:" + Thread.currentThread().getName());
        this.completeTime = System.currentTimeMillis();
        LogUtil.e("http spent time[" + (this.completeTime - this.startTime) + "]ms");
    }
}