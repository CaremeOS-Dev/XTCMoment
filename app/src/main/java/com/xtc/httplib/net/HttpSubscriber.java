package com.xtc.httplib.net;

/** Subscriber with an extra HTTP-error hook. */
public class HttpSubscriber<T> extends BaseSubscriber<T> {

    public void onHttpError(Throwable e) {
    }

    @Override
    public void onNext(T value) {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
    }

    @Override
    public final void onError(Throwable e) {
        super.onError(e);
        onHttpError(e);
    }
}