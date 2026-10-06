package com.xtc.moment.monitor;

import com.xtc.log.LogUtil;

import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 带容量与优先级裁剪策略的阻塞队列。
 */
public class IOPriorityQueue extends PriorityBlockingQueue {

    private int mQueueSize;
    private ThreadPoolExecutor mThreadPoolExecutor;

    public IOPriorityQueue(int initialCapacity, int maxQueueSize) {
        super(initialCapacity);
        this.mQueueSize = maxQueueSize;
    }

    public IOPriorityQueue(int initialCapacity, Comparator comparator, int maxQueueSize) {
        super(initialCapacity, comparator);
        this.mQueueSize = maxQueueSize;
    }

    @Override
    public boolean offer(Object task) {
        if (isNotAllowOfferQueue(task)) {
            return false;
        }
        return super.offer(task);
    }

    @Override
    public boolean offer(Object task, long timeout, TimeUnit unit) {
        if (isNotAllowOfferQueue(task)) {
            return false;
        }
        return super.offer(task, timeout, unit);
    }

    private boolean isNotAllowOfferQueue(Object task) {
        int queueSizeLimit;
        if (this.mThreadPoolExecutor == null || (task instanceof FutureTask)) {
            return false;
        }
        IBaseWork baseWork = (IBaseWork) task;
        if (baseWork.isNeedCreateNewThread()) {
            return false;
        }
        if (this.mThreadPoolExecutor.getPoolSize() < this.mThreadPoolExecutor.getMaximumPoolSize()) {
            baseWork.setNeedCreateNewThread(true);
            LogUtil.d(IOMonitorConstants.MONITOR_LOG_TAG, "need create new ");
            return true;
        }
        if (baseWork.getPriority() >= IOTaskPriorityType.DISCARD_TASK_VALUE
                || (queueSizeLimit = this.mQueueSize) <= 0
                || queueSizeLimit >= size()) {
            return false;
        }
        LogUtil.d(IOMonitorConstants.MONITOR_LOG_TAG, String.format(Locale.ENGLISH, "remove old task:%s", baseWork));
        return true;
    }

    public void setThreadPoolExecutor(ThreadPoolExecutor executor) {
        this.mThreadPoolExecutor = executor;
    }
}