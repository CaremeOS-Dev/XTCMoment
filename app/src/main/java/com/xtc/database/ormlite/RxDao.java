package com.xtc.database.ormlite;

import android.content.Context;

import com.xtc.log.LogUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/** {@link OrmLiteDao} that runs every operation on the IO scheduler and reports back on the UI thread. */
public class RxDao<T> extends OrmLiteDao<T> {

    private static final String TAG = "RxDao";

    private CompositeSubscription compositeSubscription;

    public RxDao(Context context, Class<T> clazz, String databaseName) {
        super(context, clazz, databaseName);
    }

    /** Starts collecting the subscriptions. Must be called before the first operation. */
    public void subscribe() {
        this.compositeSubscription = RxUtil.reset(this.compositeSubscription);
    }

    /** Unsubscribes every pending operation. */
    public void unsubscribe() {
        RxUtil.unsubscribe(this.compositeSubscription);
    }

    public void insert(final T item, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return insert(item);
            }
        }, callback);
    }

    public void insertForBatch(final List<T> list, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return insertForBatch(list);
            }
        }, callback);
    }

    public void clearTableData(final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return clearTableData();
            }
        }, callback);
    }

    public void deleteByColumnName(final String columnName, final Object value, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteByColumnName(columnName, value);
            }
        }, callback);
    }

    public void deleteByColumnName(final Map<String, Object> conditions, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteByColumnName(conditions);
            }
        }, callback);
    }

    public void deleteLtValue(final String columnName, final Object value, final DbCallBack callback) {
        execute(new Callable<Integer>() {
            @Override
            public Integer call() {
                return deleteLtValue(columnName, value);
            }
        }, callback);
    }

    public void deleteForBatch(final List<T> list, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return deleteForBatch(list);
            }
        }, callback);
    }

    public void update(final T item, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return update(item);
            }
        }, callback);
    }

    public void updateBy(final T item, final String columnName, final Object value, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return updateBy(item, columnName, value);
            }
        }, callback);
    }

    public void updateBy(final T item, final Map<String, Object> conditions, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return updateBy(item, conditions);
            }
        }, callback);
    }

    public void updateForBatch(final List<T> list, final DbCallBack callback) {
        execute(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                return updateForBatch(list);
            }
        }, callback);
    }

    @Override
    public long getCount(Map<String, Object> conditions) {
        return super.getCount(conditions);
    }

    public void getCount(final Map<String, Object> conditions, final DbCallBack callback) {
        execute(new Callable<Long>() {
            @Override
            public Long call() {
                return getCount(conditions);
            }
        }, callback);
    }

    @Override
    public long getCount() {
        return super.getCount();
    }

    public void getCount(final DbCallBack callback) {
        execute(new Callable<Long>() {
            @Override
            public Long call() {
                return getCount();
            }
        }, callback);
    }

    public void queryForAll(final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryForAll();
            }
        }, callback);
    }

    public void queryByColumnName(final Map<String, Object> conditions, final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryByColumnName(conditions);
            }
        }, callback);
    }

    public void queryByColumnName(final String columnName, final Object value, final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryByColumnName(columnName, value);
            }
        }, callback);
    }

    public void queryByOrder(final String orderColumn, final String columnName, final Object value,
            final boolean ascending, final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryByOrder(orderColumn, columnName, value, ascending);
            }
        }, callback);
    }

    public void queryAllBySelectColumns(final String[] columns, final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryAllBySelectColumns(columns);
            }
        }, callback);
    }

    public void queryGeByOrder(final String columnName, final Object value, final boolean ascending,
            final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryGeByOrder(columnName, value, ascending);
            }
        }, callback);
    }

    public void queryLeByOrder(final String columnName, final Object value, final boolean ascending,
            final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryLeByOrder(columnName, value, ascending);
            }
        }, callback);
    }

    public void queryForPagesByOrder(final String columnName, final Object value, final String orderColumn,
            final boolean ascending, final Long offset, final Long limit, final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryForPagesByOrder(columnName, value, orderColumn, ascending, offset, limit);
            }
        }, callback);
    }

    public void queryForPagesByOrder(final Map<String, Object> conditions, final String orderColumn,
            final boolean ascending, final Long offset, final Long limit, final DbCallBack callback) {
        execute(new Callable<List<T>>() {
            @Override
            public List<T> call() {
                return queryForPagesByOrder(conditions, orderColumn, ascending, offset, limit);
            }
        }, callback);
    }

    public void queryForFirst(final String columnName, final Object value, final DbCallBack callback) {
        execute(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirst(columnName, value);
            }
        }, callback);
    }

    public void queryForFirst(final Map<String, Object> conditions, final DbCallBack callback) {
        execute(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirst(conditions);
            }
        }, callback);
    }

    public void queryForFirstByOrder(final Map<String, Object> conditions, final String orderColumn,
            final boolean ascending, final DbCallBack callback) {
        execute(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirstByOrder(conditions, orderColumn, ascending);
            }
        }, callback);
    }

    public void queryForFirstByOrder(final String columnName, final Object value, final String orderColumn,
            final boolean ascending, final DbCallBack callback) {
        execute(new Callable<T>() {
            @Override
            public T call() {
                return queryForFirstByOrder(columnName, value, orderColumn, ascending);
            }
        }, callback);
    }

    private <R> void execute(Callable<R> callable, DbCallBack callback) {
        Subscription subscription = fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<R>() {
                    @Override
                    public void call(R result) {
                        callback.onResult(result);
                    }
                });
        CompositeSubscription subscriptions = this.compositeSubscription;
        if (subscriptions == null) {
            throw new RuntimeException("Do you call subscribe()");
        }
        subscriptions.add(subscription);
    }

    private <R> Observable<R> fromCallable(final Callable<R> callable) {
        return Observable.create(new Observable.OnSubscribe<R>() {
            @Override
            public void call(Subscriber<? super R> subscriber) {
                try {
                    subscriber.onNext(callable.call());
                    subscriber.onCompleted();
                } catch (Exception e) {
                    LogUtil.e(TAG, "Error reading from the database" + e);
                }
            }
        });
    }
}