package com.xtc.httplib.rxadapter;

import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.plugins.RxJavaPlugins;

/** Maps the response to its body, raising {@link HttpException} on failure. */
class XtcBodyOnSubscribe<T> implements Observable.OnSubscribe<T> {

    private final Observable.OnSubscribe<Response<T>> upstream;

    XtcBodyOnSubscribe(Observable.OnSubscribe<Response<T>> upstream) {
        this.upstream = upstream;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        this.upstream.call(new BodySubscriber<T>(subscriber));
    }

    private static class BodySubscriber<R> extends Subscriber<Response<R>> {
        private final Subscriber<? super R> subscriber;
        private boolean subscriberTerminated;

        BodySubscriber(Subscriber<? super R> subscriber) {
            super(subscriber);
            this.subscriber = subscriber;
        }

        @Override
        public void onNext(Response<R> response) {
            if (response.isSuccessful()) {
                this.subscriber.onNext(response.body());
                return;
            }
            this.subscriberTerminated = true;
            HttpException httpException = new HttpException(response);
            try {
                this.subscriber.onError(httpException);
            } catch (OnCompletedFailedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (OnErrorFailedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (OnErrorNotImplementedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(new CompositeException(httpException, t));
            }
        }

        @Override
        public void onError(Throwable throwable) {
            if (!this.subscriberTerminated) {
                this.subscriber.onError(throwable);
                return;
            }
            AssertionError error = new AssertionError("This should never happen! Report as a Retrofit bug with the full stacktrace.");
            error.initCause(throwable);
            RxJavaPlugins.getInstance().getErrorHandler().handleError(error);
        }

        @Override
        public void onCompleted() {
            if (this.subscriberTerminated) {
                return;
            }
            this.subscriber.onCompleted();
        }
    }
}