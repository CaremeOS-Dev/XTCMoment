package com.xtc.moment.monitor;

import android.text.TextUtils;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 全局主 IO 线程池单例，核心线程数受限并支持队列裁剪。
 */
public class LimitCoreThreadPool implements IThreadPool {

    private static final String TAG = "LimitCoreThreadPool";
    private static final String APP_WORK_THREAD_CORE = "app-running-thread-";

    private ThreadPoolExecutor mMainThreadPoolExecutor;
    private ThreadGroup mThreadGroup;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final AtomicInteger mDiscardTaskCount = new AtomicInteger(0);

    public LimitCoreThreadPool build(int corePoolSize, int maximumPoolSize, long keepAliveTime, int maxQueueSize,
            boolean allowCoreThreadTimeOut) {
        this.mThreadGroup = new ThreadGroup("app_io_thread_group");
        IOPriorityQueue priorityQueue = maxQueueSize == 0
                ? new IOPriorityQueue(100, 0)
                : new IOPriorityQueue(100, maxQueueSize);
        this.mMainThreadPoolExecutor = new MonitorThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime,
                TimeUnit.SECONDS, priorityQueue, new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        int index = LimitCoreThreadPool.this.threadNumber.getAndIncrement();
                        MonitorUtil.d(TAG, "newThread: index", Integer.valueOf(index));
                        return new CustomThread(LimitCoreThreadPool.this.mThreadGroup, runnable,
                                String.format("%s%s", APP_WORK_THREAD_CORE, Integer.valueOf(index)));
                    }
                }, new AbstractRejectedExecutionHandler() {
                    @Override
                    public void rejectedTask(Runnable runnable, ThreadPoolExecutor executor, IBaseWork baseWork) {
                        String taskName;
                        if (baseWork != null) {
                            taskName = baseWork.getTaskName();
                            if (TextUtils.isEmpty(taskName)) {
                                taskName = runnable.toString();
                            }
                        } else {
                            taskName = "";
                        }
                        LimitCoreThreadPool.this.mDiscardTaskCount.getAndIncrement();
                        String message = String.format(Locale.ENGLISH, IOMonitorConstants.TASK_GIVE_UP_TIP, taskName);
                        MonitorUtil.d(TAG, "rejectedTask: error", message);
                        rejectedExecutionException(message);
                    }
                });
        priorityQueue.setThreadPoolExecutor(this.mMainThreadPoolExecutor);
        this.mMainThreadPoolExecutor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
        return this;
    }

    private static class SingleInstance {
        private static final LimitCoreThreadPool INSTANCE = new LimitCoreThreadPool();

        private SingleInstance() {
        }
    }

    public static LimitCoreThreadPool getInstance() {
        return SingleInstance.INSTANCE;
    }

    @Override
    public ThreadPoolExecutor getMainIOThreadPoolExecutor() {
        return this.mMainThreadPoolExecutor;
    }
}