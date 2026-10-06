package com.xtc.aitext.util;

import android.os.Looper;
import android.util.Pair;

import com.xtc.log.LogUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import rx.Completable;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * AI 文案 Rx 工具，封装线程切换、延迟任务与订阅管理等常用操作。
 */
public class AITextRxUtils {

    private static final String TAG = "RxUtils";

    private static final Object TRIGGER = new Object();
    private static volatile Scheduler singleScheduler;

    private static final Action1<Throwable> LOG_ERROR = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            LogUtil.e(TAG, ":", throwable);
        }
    };

    /** 若当前为主线程则切换到 IO 线程。 */
    public static final Observable.Transformer<Object, Object> TO_IO_IF_MAIN = new Observable.Transformer<Object, Object>() {
        @Override
        public Observable<Object> call(Observable<Object> observable) {
            return Thread.currentThread() != Looper.getMainLooper().getThread()
                    ? observable : observable.subscribeOn(Schedulers.io());
        }
    };

    /** 若当前非主线程则切换到主线程。 */
    public static final Observable.Transformer<Object, Object> TO_MAIN_IF_BACKGROUND = new Observable.Transformer<Object, Object>() {
        @Override
        public Observable<Object> call(Observable<Object> observable) {
            return Thread.currentThread() == Looper.getMainLooper().getThread()
                    ? observable : observable.subscribeOn(AndroidSchedulers.mainThread());
        }
    };

    public static Action0 logCompleted(final String tag) {
        return new Action0() {
            @Override
            public void call() {
                LogUtil.d(tag, "onCompleted");
            }
        };
    }

    public static <T> Action1<T> emptyAction() {
        return Actions.empty();
    }

    public static Action0 emptyAction0() {
        return Actions.empty();
    }

    /** 单线程调度器。 */
    public static Scheduler getSingleScheduler() {
        if (singleScheduler == null) {
            synchronized (AITextRxUtils.class) {
                if (singleScheduler == null) {
                    singleScheduler = Schedulers.from(Executors.newSingleThreadExecutor());
                }
            }
        }
        return singleScheduler;
    }

    public static <T> Action1<T> logNext(final String tag) {
        return new Action1<T>() {
            @Override
            public void call(T value) {
                LogUtil.i(tag, "onNext:" + value);
            }
        };
    }

    public static Action1<Throwable> logError() {
        return LOG_ERROR;
    }

    public static Action1<Throwable> logError(final String tag) {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtil.e(tag, "onError: ", throwable);
            }
        };
    }

    public static <T> Observable<T> fromSupplier(final Supplier<T> supplier) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                subscriber.onNext(supplier.get());
                subscriber.onCompleted();
            }
        });
    }

    public static void runAsync(String tag, Action0 action) {
        Completable.fromAction(action).subscribeOn(Schedulers.io())
                .subscribe(logCompleted(tag), logError(tag));
    }

    public static Completable completableFrom(Action0 action) {
        return Completable.fromAction(action).subscribeOn(Schedulers.io());
    }

    /** 在主线程执行任务。 */
    public static void runOnMain(final Runnable runnable) {
        Observable.just(TRIGGER).compose(TO_MAIN_IF_BACKGROUND)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                })
                .subscribe(emptyAction(), logError(TAG));
    }

    public static <T> Observable<T> fromCallable(final Callable<T> callable) {
        return Observable.just(TRIGGER).compose(TO_MAIN_IF_BACKGROUND)
                .map(new Func1<Object, T>() {
                    @Override
                    public T call(Object value) {
                        try {
                            return callable.call();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    public static <T> Observable<T> fromCallableOnIo(final Callable<T> callable) {
        return Observable.just(TRIGGER).compose(TO_IO_IF_MAIN)
                .map(new Func1<Object, T>() {
                    @Override
                    public T call(Object value) {
                        try {
                            return callable.call();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    /** 在 IO 线程执行任务。 */
    public static void runOnIo(final Runnable runnable) {
        Observable.just(TRIGGER).compose(TO_IO_IF_MAIN)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                })
                .subscribe(emptyAction(), logError(TAG));
    }

    public static void runOnIoBlocking(final Runnable runnable) {
        Observable.just(TRIGGER).compose(TO_IO_IF_MAIN)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                })
                .toBlocking().last();
    }

    public static void runDelay(long delay, TimeUnit unit, final Runnable runnable) {
        Observable.just(TRIGGER).delay(delay, unit).subscribeOn(Schedulers.io())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                })
                .subscribe(emptyAction(), logError(TAG));
    }

    public static Observable<Object> delayObservable(long delay, TimeUnit unit, final Runnable runnable) {
        return Observable.just(TRIGGER).delay(delay, unit).subscribeOn(Schedulers.io())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                });
    }

    public static void runDelayOnMain(long delay, TimeUnit unit, final Runnable runnable) {
        Observable.just(TRIGGER).delay(delay, unit).subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                })
                .subscribe(emptyAction(), logError(TAG));
    }

    public static void runOnMainDelay(final Runnable runnable, long delayMillis) {
        Observable.just(TRIGGER).delay(delayMillis, TimeUnit.MILLISECONDS).subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                })
                .subscribe(emptyAction(), logError(TAG));
    }

    public static void runOnIoDelay(final Runnable runnable, long delayMillis) {
        Observable.just(TRIGGER).delay(delayMillis, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                })
                .subscribe(emptyAction(), logError(TAG));
    }

    public static Observable<Object> ioObservable(final Runnable runnable) {
        return Observable.just(TRIGGER).compose(TO_IO_IF_MAIN)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                });
    }

    public static Observable<Object> mainObservable(final Runnable runnable) {
        return Observable.just(TRIGGER).compose(TO_MAIN_IF_BACKGROUND)
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                });
    }

    public static Observable<Object> mainDelayObservable(long delay, TimeUnit unit, final Runnable runnable) {
        return Observable.just(TRIGGER).delay(delay, unit).subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Object, Object>() {
                    @Override
                    public Object call(Object value) {
                        runnable.run();
                        return value;
                    }
                });
    }

    public static <T, V> Observable<Pair<T, V>> fromCallablePair(final Callable<Pair<T, V>> callable) {
        return Observable.just(TRIGGER).compose(TO_IO_IF_MAIN)
                .map(new Func1<Object, Pair<T, V>>() {
                    @Override
                    public Pair<T, V> call(Object value) {
                        try {
                            return callable.call();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
    }

    public static void unsubscribe(Subscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return;
        }
        subscription.unsubscribe();
    }
}