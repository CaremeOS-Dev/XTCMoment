package com.xtc.dispatch.scheduler;

import android.util.Log;

import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.task.AbsTask;

/** Runs a dispatched task on its scheduler and reports the completion back. */
public class SchedulerRunnable implements Runnable {

    private static final String TAG = "SchedulerRunnable";

    private final AbsTask task;
    private final TaskDispatcher taskDispatcher;

    public SchedulerRunnable(AbsTask task, TaskDispatcher taskDispatcher) {
        this.task = task;
        this.taskDispatcher = taskDispatcher;
    }

    @Override
    public void run() {
        this.task.nextTaskState();
        Log.i(TAG, "run task:" + this.task);
        if (this.task.getThreadType() == AbsTask.ThreadType.MAIN) {
            if (this.task.getDelayTime() > 0) {
                AbsTask.uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runTask();
                    }
                }, this.task.getDelayTime());
                return;
            }
            runTask();
            return;
        }
        if (this.task.getDelayTime() > 0) {
            try {
                synchronized (this) {
                    wait(this.task.getDelayTime());
                }
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
        }
        this.task.await();
        runTask();
    }

    private void runTask() {
        this.task.nextTaskState();
        Log.d(TAG, "run task:" + this.task);
        this.task.run();
        this.task.nextTaskState();
        Log.d(TAG, "run task:" + this.task);
        if (this.taskDispatcher != null) {
            this.taskDispatcher.onTaskCompleted(this.task);
            this.taskDispatcher.markTaskDispatched(this.task);
        }
    }
}