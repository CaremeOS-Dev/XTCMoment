package com.xtc.httplib.rxadapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.Result;
import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Single;

/** Call-adapter factory producing the XTC RxJava types. */
public class XtcRxJavaCallAdapterFactory extends CallAdapter.Factory {

    private final boolean isAsync;
    private final Scheduler scheduler;

    public static XtcRxJavaCallAdapterFactory create() {
        return new XtcRxJavaCallAdapterFactory(null, false);
    }

    public static XtcRxJavaCallAdapterFactory createAsync() {
        return new XtcRxJavaCallAdapterFactory(null, true);
    }

    public static XtcRxJavaCallAdapterFactory createWithScheduler(Scheduler scheduler) {
        if (scheduler == null) {
            throw new NullPointerException("scheduler == null");
        }
        return new XtcRxJavaCallAdapterFactory(scheduler, false);
    }

    private XtcRxJavaCallAdapterFactory(Scheduler scheduler, boolean isAsync) {
        this.scheduler = scheduler;
        this.isAsync = isAsync;
    }

    @Override
    public CallAdapter<?, ?> get(Type type, Annotation[] annotations, Retrofit retrofit) {
        Class<?> rawType = getRawType(type);
        boolean isSingle = rawType == Single.class;
        boolean isCompletable = rawType == Completable.class;
        if (rawType != Observable.class && !isSingle && !isCompletable) {
            return null;
        }
        if (isCompletable) {
            return new XtcRxJavaCallAdapter(Void.class, this.scheduler, this.isAsync, false, true, false, true);
        }
        if (!(type instanceof ParameterizedType)) {
            String name = isSingle ? "Single" : "Observable";
            throw new IllegalStateException(name + " return type must be parameterized as " + name + "<Foo> or " + name + "<? extends Foo>");
        }
        Type innerType = getParameterUpperBound(0, (ParameterizedType) type);
        Class<?> innerRawType = getRawType(innerType);
        if (innerRawType == Response.class) {
            if (!(innerType instanceof ParameterizedType)) {
                throw new IllegalStateException("Response must be parameterized as Response<Foo> or Response<? extends Foo>");
            }
            return new XtcRxJavaCallAdapter(getParameterUpperBound(0, (ParameterizedType) innerType),
                    this.scheduler, this.isAsync, false, false, isSingle, false);
        }
        if (innerRawType == Result.class) {
            if (!(innerType instanceof ParameterizedType)) {
                throw new IllegalStateException("Result must be parameterized as Result<Foo> or Result<? extends Foo>");
            }
            return new XtcRxJavaCallAdapter(getParameterUpperBound(0, (ParameterizedType) innerType),
                    this.scheduler, this.isAsync, true, false, isSingle, false);
        }
        return new XtcRxJavaCallAdapter(innerType, this.scheduler, this.isAsync, false, true, isSingle, false);
    }
}