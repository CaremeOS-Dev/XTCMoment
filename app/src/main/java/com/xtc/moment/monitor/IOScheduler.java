package com.xtc.moment.monitor;

import rx.Scheduler;

/**
 * 基于 IO 监控线程池的 RxJava 调度器。
 */
public class IOScheduler extends AbstractScheduler {

    private String mNewTaskTag;
    private int priority;

    public IOScheduler() {
        this.mNewTaskTag = null;
        this.priority = 0;
    }

    public IOScheduler(String newTaskTag, int priority) {
        this.mNewTaskTag = null;
        this.priority = 0;
        this.mNewTaskTag = newTaskTag;
        this.priority = priority;
    }

    @Override
    public AbstractScheduler create(String taskTag, int priority) {
        return new IOScheduler(taskTag, priority);
    }

    @Override
    public Scheduler.Worker createNewWorker(String taskTag) {
        return MonitorUtil.createNewWorker(taskTag, this.priority);
    }

    @Override
    public String getNewTaskTag() {
        return this.mNewTaskTag;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }
}