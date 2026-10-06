package com.xtc.dispatch.scheduler;

import android.os.Process;
import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Fixed thread pool used for the IO tasks. */
public class IOTaskScheduler implements ITaskScheduler {

    private static final String TAG = "IOTaskScheduler";
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(PROCESSOR_COUNT, 4));

    private static IOTaskScheduler instance;

    private final ThreadPoolExecutor executor;

    public static IOTaskScheduler getInstance() {
        if (instance == null) {
            instance = new IOTaskScheduler();
        }
        return instance;
    }

    private IOTaskScheduler() {
        Log.d(TAG, "IOTaskScheduler CORE_POOL_SIZE:" + CORE_POOL_SIZE);
        this.executor = new ThreadPoolExecutor(CORE_POOL_SIZE, CORE_POOL_SIZE, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
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
                thread.setName("IOThreadPool-" + thread.getName());
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