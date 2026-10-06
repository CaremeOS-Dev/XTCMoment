package com.xtc.dispatch.scheduler;

import android.os.Process;
import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Single-thread pool used to serialize the tasks that must not run concurrently. */
public class SingleTaskScheduler implements ITaskScheduler {

    private static final String TAG = "SingleTaskScheduler";

    private static SingleTaskScheduler instance;

    private final ThreadPoolExecutor executor;

    public static SingleTaskScheduler getInstance() {
        if (instance == null) {
            instance = new SingleTaskScheduler();
        }
        return instance;
    }

    private SingleTaskScheduler() {
        Log.d(TAG, TAG);
        this.executor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.executor.setThreadFactory(new ThreadFactory() {
            @Override
            public Thread newThread(final Runnable runnable) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                        runnable.run();
                    }
                });
                thread.setName("SingleThreadPool-" + thread.getName());
                return thread;
            }
        });
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