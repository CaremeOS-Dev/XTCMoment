package com.xtc.moment.monitor;

import rx.Scheduler;

/**
 * RxJava 调度器基类，按任务名与优先级创建 Worker。
 */
public abstract class AbstractScheduler extends Scheduler {

    public static final String TASK_NAME = "xtc_worker";

    public abstract AbstractScheduler create(String taskTag, int priority);

    public abstract Scheduler.Worker createNewWorker(String taskTag);

    public abstract String getNewTaskTag();

    public abstract int getPriority();

    @Override
    public Scheduler.Worker createWorker() {
        return createNewWorker(TASK_NAME);
    }
}