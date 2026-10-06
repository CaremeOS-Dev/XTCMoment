package com.xtc.moment.monitor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 带监控埋点的线程池，在任务执行前后输出线程池状态。
 */
public abstract class MonitorThreadPoolExecutor extends ThreadPoolExecutor {

    private static final String TAG = "MonitorThreadPoolExecutor";

    public MonitorThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
            BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
            RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable runnable) {
        MonitorUtil.logBeforeExecute(TAG, thread, runnable);
        super.beforeExecute(thread, runnable);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable) {
        MonitorUtil.d(TAG, "afterExecute : ", Boolean.valueOf(throwable == null));
        super.afterExecute(runnable, throwable);
    }

    @Override
    public String toString() {
        return "[Running pool size = " + getPoolSize()
                + ",max pool size = " + getMaximumPoolSize()
                + ",active task count = " + getActiveCount()
                + ",queued tasks = " + getQueue().size()
                + ",completed tasks = " + getCompletedTaskCount()
                + ",all task count:" + getTaskCount()
                + ",core thread=" + getCorePoolSize()
                + ",allow core thread time out=" + allowsCoreThreadTimeOut() + "]";
    }

    @Override
    public void execute(Runnable command) {
        MonitorUtil.logThreadPoolExecute(TAG, this);
        super.execute(command);
    }
}