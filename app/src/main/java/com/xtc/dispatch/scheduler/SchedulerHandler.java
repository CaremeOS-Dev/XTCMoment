package com.xtc.dispatch.scheduler;

import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;

import java.util.concurrent.Future;

/** Picks the scheduler matching the task thread type and runs it. */
public class SchedulerHandler {

    public static <Q extends AbsTask.RequestValues, R extends AbsTask.ResponseValue> void schedule(
            AbsTask<Q, R> task, TaskDispatcher taskDispatcher) {
        getScheduler(task).execute(new SchedulerRunnable(task, taskDispatcher));
    }

    public static <Q extends AbsTask.RequestValues, R extends AbsTask.ResponseValue> Future<?> scheduleFuture(
            AbsTask<Q, R> task, TaskDispatcher taskDispatcher) {
        return getScheduler(task).submit(new SchedulerRunnable(task, taskDispatcher));
    }

    public static ITaskScheduler getScheduler(AbsTask task) {
        switch (task.getThreadType()) {
            case AbsTask.ThreadType.NORMAL:
                return CachedTaskScheduler.getInstance();
            case AbsTask.ThreadType.IO:
                return IOTaskScheduler.getInstance();
            case AbsTask.ThreadType.SINGLE:
                return SingleTaskScheduler.getInstance();
            case AbsTask.ThreadType.MAIN:
                return MainThreadTaskScheduler.getInstance();
            default:
                return CachedTaskScheduler.getInstance();
        }
    }
}