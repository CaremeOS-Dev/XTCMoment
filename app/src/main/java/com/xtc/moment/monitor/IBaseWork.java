package com.xtc.moment.monitor;

/**
 * 可被优先级线程池调度的任务抽象。
 */
public interface IBaseWork {
    int getPriority();

    String getTaskName();

    boolean isNeedCreateNewThread();

    void setNeedCreateNewThread(boolean needCreateNewThread);
}