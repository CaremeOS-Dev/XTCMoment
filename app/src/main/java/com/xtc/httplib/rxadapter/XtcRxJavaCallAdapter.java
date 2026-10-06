package com.xtc.httplib.rxadapter;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import rx.Observable;
import rx.Scheduler;

/** Retrofit call adapter producing the XTC RxJava observables. */
public class XtcRxJavaCallAdapter<R> implements CallAdapter<R, Object> {

    private final boolean isAsync;
    private final boolean isBody;
    private final boolean isCompletable;
    private final boolean isResult;
    private final boolean isSingle;
    private final Type responseType;
    private final Scheduler scheduler;

    XtcRxJavaCallAdapter(Type responseType, Scheduler scheduler, boolean isAsync, boolean isResult,
                         boolean isBody, boolean isSingle, boolean isCompletable) {
        this.responseType = responseType;
        this.scheduler = scheduler;
        this.isAsync = isAsync;
        this.isResult = isResult;
        this.isBody = isBody;
        this.isSingle = isSingle;
        this.isCompletable = isCompletable;
    }

    @Override
    public Type responseType() {
        return this.responseType;
    }

    @Override
    public Object adapt(Call<R> call) {
        Observable.OnSubscribe onSubscribe = this.isAsync
                ? new XtcCallEnqueueOnSubscribe<R>(call) : new XtcCallExecuteOnSubscribe<R>(call);
        Observable.OnSubscribe resultOnSubscribe;
        if (this.isResult) {
            resultOnSubscribe = new XtcResultOnSubscribe<R>(onSubscribe);
        } else {
            resultOnSubscribe = this.isBody ? new XtcBodyOnSubscribe<R>(onSubscribe) : onSubscribe;
        }
        Observable observable = Observable.create(resultOnSubscribe);
        Scheduler scheduler = this.scheduler;
        if (scheduler != null) {
            observable = observable.subscribeOn(scheduler);
        }
        if (this.isSingle) {
            return observable.toSingle();
        }
        return this.isCompletable ? observable.toCompletable() : observable;
    }
}