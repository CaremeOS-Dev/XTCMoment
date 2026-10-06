package com.xtc.httplib.rxadapter;

import retrofit2.Response;
import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.plugins.RxJavaPlugins;

/** Wraps the response in a {@link Result} instead of raising. */
public class XtcResultOnSubscribe<T> implements Observable.OnSubscribe<Result<T>> {

    private final Observable.OnSubscribe<Response<T>> upstream;

    XtcResultOnSubscribe(Observable.OnSubscribe<Response<T>> upstream) {
        this.upstream = upstream;
    }

    @Override
    public void call(Subscriber<? super Result<T>> subscriber) {
        this.upstream.call(new ResultSubscriber<T>(subscriber));
    }

    private static class ResultSubscriber<R> extends Subscriber<Response<R>> {
        private final Subscriber<? super Result<R>> subscriber;

        ResultSubscriber(Subscriber<? super Result<R>> subscriber) {
            super(subscriber);
            this.subscriber = subscriber;
        }

        @Override
        public void onNext(Response<R> response) {
            this.subscriber.onNext(Result.response(response));
        }

        @Override
        public void onError(Throwable throwable) {
            try {
                this.subscriber.onNext(Result.error(throwable));
                this.subscriber.onCompleted();
            } catch (Throwable t) {
                try {
                    this.subscriber.onError(t);
                } catch (OnCompletedFailedException e) {
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                } catch (OnErrorFailedException e) {
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                } catch (OnErrorNotImplementedException e) {
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
                } catch (Throwable inner) {
                    Exceptions.throwIfFatal(inner);
                    RxJavaPlugins.getInstance().getErrorHandler().handleError(new CompositeException(t, inner));
                }
            }
        }

        @Override
        public void onCompleted() {
            this.subscriber.onCompleted();
        }
    }
}