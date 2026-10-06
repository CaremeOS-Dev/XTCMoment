package com.xtc.dispatch.scheduler;

import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Single-thread pool used to serialize the RxJava related tasks. */
public class RxJavaScheduler implements ITaskScheduler {

    private static final String TAG = "RxJavaScheduler";

    private static RxJavaScheduler instance;

    private final ThreadPoolExecutor executor;

    public static RxJavaScheduler getInstance() {
        if (instance == null) {
            instance = new RxJavaScheduler();
        }
        return instance;
    }

    private RxJavaScheduler() {
        Log.d(TAG, TAG);
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    @Override
    public void execute(Runnable runnable) {
        this.executor.execute(runnable);
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        return this.executor.submit(runnable);
    }
}