package com.xtc.moment.monitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * IO 任务优先级取值（仅编译期保留）。
 */
@Retention(RetentionPolicy.SOURCE)
public @interface IOTaskPriorityType {
    int CORE_TASK = 100;
    int DATABASE_IO_TASK = 50;
    int DISCARD_TASK_VALUE = -50;
    int LOAD_DATA_FOR_VIEW_TASK = 60;
    int LOW_PRIORITY_TASK = -100;
    int NETWORK_TASK = 20;
    int NORMAL_TASK = 0;
    int UPDATE_VIEW_TASK = -80;
}