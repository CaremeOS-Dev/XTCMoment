package com.xtc.dispatch.scheduler;

import android.os.Process;
import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Unbounded cached thread pool used for the normal tasks. */
public class CachedTaskScheduler implements ITaskScheduler {

    private static final String TAG = "CachedTaskScheduler";
    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = Math.max(2, Math.min(PROCESSORS, 4)) * 2;

    private static CachedTaskScheduler instance;

    private final ThreadPoolExecutor executor;

    public static CachedTaskScheduler getInstance() {
        if (instance == null) {
            instance = new CachedTaskScheduler();
        }
        return instance;
    }

    private CachedTaskScheduler() {
        Log.d(TAG, "CachedTaskScheduler CORE_POOL_SIZE:" + MAX_POOL_SIZE);
        this.executor = new ThreadPoolExecutor(2, MAX_POOL_SIZE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
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
                thread.setName("CachedThreadPool-" + thread.getName());
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