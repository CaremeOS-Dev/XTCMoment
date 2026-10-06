package com.xtc.moment.monitor;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池拒绝策略基类，把 Runnable 还原成 {@link IBaseWork} 后回调给子类处理。
 */
public abstract class AbstractRejectedExecutionHandler implements RejectedExecutionHandler {

    public abstract void rejectedTask(Runnable runnable, ThreadPoolExecutor executor, IBaseWork baseWork);

    @Override
    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
        rejectedTask(runnable, executor, runnable instanceof IBaseWork ? (IBaseWork) runnable : null);
    }

    public void rejectedExecutionException(String message) {
        throw new RejectedExecutionException(message);
    }
}