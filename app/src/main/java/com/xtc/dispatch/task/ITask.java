package com.xtc.dispatch.task;

/** Runnable unit of work tracked by the dispatcher. */
public interface ITask {

    /** Blocks until every dependency has completed. */
    void await();

    /** Marks one dependency as completed. */
    void countDown();

    /** Runs the task body. */
    void run();
}