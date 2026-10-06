package com.xtc.httplib.rxadapter;

import com.xtc.httplib.LogTag;

import retrofit2.Call;
import retrofit2.Response;
import rx.Subscriber;
import rx.exceptions.Exceptions;

/** Synchronous variant of the custom on-subscribe. */
public class XtcCallExecuteOnSubscribe<T> extends BaseXtcOnSubscribe<T> {

    private static final String TAG = LogTag.tag("XtcCallExecuteOnSubscribe");

    XtcCallExecuteOnSubscribe(Call<T> call) {
        super(call);
    }

    @Override
    public void callRealRequest(Subscriber<? super Response<T>> subscriber, OnSubscribeCallBack callback) {
        Call<T> call = this.originalCall.clone();
        XtcCallArbiter<T> arbiter = new XtcCallArbiter<>(call, subscriber);
        subscriber.add(arbiter);
        subscriber.setProducer(arbiter);
        try {
            arbiter.emitResponse(call.execute());
            if (callback != null) {
                callback.onComplete();
            }
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            arbiter.emitError(t);
            if (callback != null) {
                callback.onComplete();
            }
        }
    }
}