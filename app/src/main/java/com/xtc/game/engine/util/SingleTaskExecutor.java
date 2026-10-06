package com.xtc.game.engine.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 单线程任务执行器，保证任务按提交顺序执行。
 */
public class SingleTaskExecutor {

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    public static void schedule(Runnable runnable, long delayMillis) {
        EXECUTOR.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
    }

    public static void submit(Runnable runnable) {
        EXECUTOR.submit(runnable);
    }
}