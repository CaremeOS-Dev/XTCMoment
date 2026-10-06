package com.xtc.httplib.rxadapter;

import com.xtc.httplib.LogTag;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.exceptions.Exceptions;

/** Asynchronous variant of the custom on-subscribe. */
public class XtcCallEnqueueOnSubscribe<T> extends BaseXtcOnSubscribe<T> {

    private static final String TAG = LogTag.tag("XtcCallEnqueueOnSubscribe");

    XtcCallEnqueueOnSubscribe(Call<T> call) {
        super(call);
    }

    @Override
    public void callRealRequest(Subscriber<? super Response<T>> subscriber, final OnSubscribeCallBack callback) {
        Call<T> call = this.originalCall.clone();
        final XtcCallArbiter<T> arbiter = new XtcCallArbiter<>(call, subscriber);
        subscriber.add(arbiter);
        subscriber.setProducer(arbiter);
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                arbiter.emitResponse(response);
                if (callback != null) {
                    callback.onComplete();
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                Exceptions.throwIfFatal(throwable);
                arbiter.emitError(throwable);
                if (callback != null) {
                    callback.onComplete();
                }
            }
        });
    }
}