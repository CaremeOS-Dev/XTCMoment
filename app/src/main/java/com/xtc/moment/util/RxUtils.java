package com.xtc.moment.util;

import com.xtc.log.LogUtil;

import java.util.function.Supplier;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/** RxJava helpers of the moment app. */
public class RxUtils {

    private RxUtils() {
    }

    public static Action0 logCompleted(final String tag) {
        return new Action0() {
            @Override
            public void call() {
                LogUtil.d(tag, "onCompleted");
            }
        };
    }

    public static <T> Action1<T> logNext(final String tag) {
        return new Action1<T>() {
            @Override
            public void call(T value) {
                LogUtil.i(tag, "onNext:" + value);
            }
        };
    }

    public static Action1<Throwable> logError(final String tag) {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(tag, "onError: ", throwable);
            }
        };
    }

    /** @return an observable that emits the value supplied by [supplier]. */
    public static <T> Observable<T> just(final Supplier<T> supplier) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(supplier.get());
                subscriber.onCompleted();
            }
        });
    }
}