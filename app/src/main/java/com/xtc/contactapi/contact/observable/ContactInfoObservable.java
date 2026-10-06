package com.xtc.contactapi.contact.observable;

import com.xtc.contactapi.base.BaseResponse;
import com.xtc.contactapi.contact.observable.interfaces.IContactSubscribe;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 联系人信息 Observable 封装，提供订阅线程与观察线程的灵活配置。
 */
public class ContactInfoObservable {

    private Observable<BaseResponse> observable;
    private IContactSubscribe subscribe;
    private Scheduler subscribeScheduler;
    private Scheduler observeScheduler;

    private ContactInfoObservable() {
        this.subscribeScheduler = Schedulers.io();
        this.observeScheduler = AndroidSchedulers.mainThread();
        this.observable = Observable.create(new Observable.OnSubscribe<BaseResponse>() {
            @Override
            public void call(Subscriber<? super BaseResponse> subscriber) {
                if (ContactInfoObservable.this.subscribe != null) {
                    ContactInfoObservable.this.subscribe.subscribe(subscriber);
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

    public IContactSubscribe getSubscribe() {
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
    public static class ContactInfoObservableBuilder {

        private final ContactInfoObservable observable = new ContactInfoObservable();

        public ContactInfoObservableBuilder subscribe(IContactSubscribe subscribe) {
            this.observable.subscribe = subscribe;
            return this;
        }

        public ContactInfoObservableBuilder subscribeScheduler(Scheduler scheduler) {
            this.observable.subscribeScheduler = scheduler;
            return this;
        }

        public ContactInfoObservableBuilder observeScheduler(Scheduler scheduler) {
            this.observable.observeScheduler = scheduler;
            return this;
        }

        public Observable<BaseResponse> build() {
            return this.observable.getObservable();
        }
    }
}