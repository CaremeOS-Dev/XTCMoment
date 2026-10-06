package com.xtc.moment.util;

import android.view.View;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;

/**
 * 把 View 的点击事件转换为 RxJava 的 {@link Observable}，并保证在取消订阅时移除点击监听。
 */
public class RxViewOnClick implements Observable.OnSubscribe<View> {

    private final View mView;

    private RxViewOnClick(View view) {
        this.mView = view;
    }

    public static Observable<View> with(View view) {
        return Observable.create(new RxViewOnClick(view));
    }

    @Override
    public void call(final Subscriber<? super View> subscriber) {
        MainThreadSubscription.verifyMainThread();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                subscriber.onNext(view);
            }
        };
        subscriber.add(new MainThreadSubscription() {
            @Override
            protected void onUnsubscribe() {
                RxViewOnClick.this.mView.setOnClickListener(null);
            }
        });
        this.mView.setOnClickListener(onClickListener);
    }
}