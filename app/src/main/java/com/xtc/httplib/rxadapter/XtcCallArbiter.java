package com.xtc.httplib.rxadapter;

import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Response;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.plugins.RxJavaPlugins;

/** Coordinates a single retrofit call with an RxJava subscriber. */
public class XtcCallArbiter<T> extends AtomicInteger implements Producer, Subscription {

    private static final int STATE_WAITING = 0;
    private static final int STATE_REQUESTED = 1;
    private static final int STATE_HAS_RESPONSE = 2;
    private static final int STATE_TERMINATED = 3;

    private final Call<T> call;
    private volatile Response<T> response;
    private final Subscriber<? super Response<T>> subscriber;
    private volatile boolean unsubscribed;

    XtcCallArbiter(Call<T> call, Subscriber<? super Response<T>> subscriber) {
        super(0);
        this.call = call;
        this.subscriber = subscriber;
    }

    @Override
    public void unsubscribe() {
        this.unsubscribed = true;
        this.call.cancel();
    }

    @Override
    public boolean isUnsubscribed() {
        return this.unsubscribed;
    }

    @Override
    public void request(long amount) {
        if (amount == 0) {
            return;
        }
        while (true) {
            int state = get();
            if (state != STATE_WAITING) {
                if (state == STATE_REQUESTED) {
                    return;
                }
                if (state == STATE_HAS_RESPONSE) {
                    if (compareAndSet(STATE_HAS_RESPONSE, STATE_TERMINATED)) {
                        deliverResponse(this.response);
                        return;
                    }
                } else if (state == STATE_TERMINATED) {
                    return;
                } else {
                    throw new IllegalStateException("Unknown state: " + state);
                }
            } else if (compareAndSet(STATE_WAITING, STATE_REQUESTED)) {
                return;
            }
        }
    }

    /** Publishes the response to the subscriber. */
    void emitResponse(Response<T> response) {
        while (true) {
            int state = get();
            if (state == STATE_WAITING) {
                this.response = response;
                if (compareAndSet(STATE_WAITING, STATE_HAS_RESPONSE)) {
                    return;
                }
            } else if (state == STATE_REQUESTED) {
                if (compareAndSet(STATE_REQUESTED, STATE_TERMINATED)) {
                    deliverResponse(response);
                    return;
                }
            } else if (state == STATE_HAS_RESPONSE || state == STATE_TERMINATED) {
                throw new AssertionError();
            } else {
                throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    private void deliverResponse(Response<T> response) {
        try {
            if (!isUnsubscribed()) {
                this.subscriber.onNext(response);
            }
            try {
                if (isUnsubscribed()) {
                    return;
                }
                this.subscriber.onCompleted();
            } catch (OnCompletedFailedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (OnErrorFailedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (OnErrorNotImplementedException e) {
                RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                RxJavaPlugins.getInstance().getErrorHandler().handleError(t);
            }
        } catch (OnCompletedFailedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (OnErrorFailedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (OnErrorNotImplementedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
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

    /** Publishes an error to the subscriber. */
    void emitError(Throwable throwable) {
        set(STATE_TERMINATED);
        if (isUnsubscribed()) {
            return;
        }
        try {
            this.subscriber.onError(throwable);
        } catch (OnCompletedFailedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (OnErrorFailedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (OnErrorNotImplementedException e) {
            RxJavaPlugins.getInstance().getErrorHandler().handleError(e);
        } catch (Throwable inner) {
            Exceptions.throwIfFatal(inner);
            RxJavaPlugins.getInstance().getErrorHandler().handleError(new CompositeException(throwable, inner));
        }
    }
}