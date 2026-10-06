package com.xtc.httplib.rxadapter;

import rx.Observable;
import rx.Subscriber;

/** Custom {@link Observable.OnSubscribe} that can react to token expiry. */
public interface XtcOnSubscribe<T> extends Observable.OnSubscribe<T> {
    void callOnTokenExpireError(Subscriber<? super T> subscriber);

    void callRealRequest(Subscriber<? super T> subscriber, OnSubscribeCallBack callback);
}