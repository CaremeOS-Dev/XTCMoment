package com.xtc.dispatch.scheduler;

import java.util.concurrent.Future;

/** Executes the dispatched tasks on a concrete thread pool. */
public interface ITaskScheduler {

    /** Number of processors available to the runtime. */
    int PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();

    void execute(Runnable runnable);

    Future<?> submit(Runnable runnable);
}