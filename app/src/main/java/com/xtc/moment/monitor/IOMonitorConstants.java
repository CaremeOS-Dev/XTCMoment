package com.xtc.moment.monitor;

/**
 * IO 调度监控相关常量。
 */
public class IOMonitorConstants {
    public static final String DISCARD_TASK_PREFIX = "task peak--discard task";
    public static final String IO_TASK_NAME_SUFFIX = "(exist_stackTrace_task)";
    public static final String MONITOR_LOG_TAG = "app_io_scheduler";
    public static final String MONITOR_TAG = "monitor_";
    public static final String TASK_GIVE_UP_TIP = "task peak,thread pool and task queue was full,this task give up :%s";
}