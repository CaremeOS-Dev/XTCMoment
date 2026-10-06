package com.xtc.contactapi.contact.observable;

import com.xtc.contactapi.base.BaseResponse;
import com.xtc.contactapi.contact.observable.interfaces.IDefaultSubscribe;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 默认头像信息 Observable 封装。
 */
public class HeadInfoObservable {

    private Observable<BaseResponse> observable;
    private IDefaultSubscribe subscribe;
    private Scheduler subscribeScheduler;
    private Scheduler observeScheduler;

    private HeadInfoObservable() {
        this.subscribeScheduler = Schedulers.io();
        this.observeScheduler = AndroidSchedulers.mainThread();
        this.observable = Observable.create(new Observable.OnSubscribe<BaseResponse>() {
            @Override
            public void call(Subscriber<? super BaseResponse> subscriber) {
                if (HeadInfoObservable.this.subscribe != null) {
                    HeadInfoObservable.this.subscribe.subscribe(subscriber);
                }
            }
        }).subscribeOn(subscribeScheduler).observeOn(observeScheduler)
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends BaseResponse>>() {
                    @Override
                    public Observable<? extends BaseResponse> call(Throwable throwable) {
                        BaseResponse response = new BaseResponse();
                        response.setResponseCode(500);
                        response.setErrorDesc(throwable.getMessage());
                        return Observable.just(response);
                    }
                });
    }

    public Observable<BaseResponse> getObservable() {
        this.observable.subscribeOn(subscribeScheduler).observeOn(observeScheduler);
        return this.observable;
    }

    public IDefaultSubscribe getSubscribe() {
        return subscribe;
    }

    public Scheduler getSubscribeScheduler() {
        return subscribeScheduler;
    }

    public Scheduler getObserveScheduler() {
        return observeScheduler;
    }

    /**
     * 构建器。
     */
    public static class HeadInfoObservableBuilder {

        private final HeadInfoObservable observable = new HeadInfoObservable();

        public HeadInfoObservableBuilder subscribe(IDefaultSubscribe subscribe) {
            this.observable.subscribe = subscribe;
            return this;
        }

        public HeadInfoObservableBuilder subscribeScheduler(Scheduler scheduler) {
            this.observable.subscribeScheduler = scheduler;
            return this;
        }

        public HeadInfoObservableBuilder observeScheduler(Scheduler scheduler) {
            this.observable.observeScheduler = scheduler;
            return this;
        }

        public Observable<BaseResponse> build() {
            return this.observable.getObservable();
        }
    }
}