package com.xtc.dispatch.task;

import android.os.Handler;
import android.os.Looper;

import com.xtc.dispatch.task.AbsTask.RequestValues;
import com.xtc.dispatch.task.AbsTask.ResponseValue;

/** Task with typed request/response values and a completion callback. */
public abstract class AbsTask<Q extends RequestValues, R extends ResponseValue> extends AbsTaskRun {

    public static final Handler uiHandler = new Handler(Looper.getMainLooper());

    /** Marker for the task input. */
    public interface RequestValues {
    }

    /** Marker for the task output. */
    public interface ResponseValue {
    }

    private long delayTime = 0;
    private Q requestValues;
    private TaskCallback<R> taskCallback;

    /** Runs the task body with the configured request values. */
    protected abstract void executeTask(Q requestValues);

    @Override
    public void run() {
        executeTask(this.requestValues);
    }

    public void setRequestValues(Q requestValues) {
        this.requestValues = requestValues;
    }

    public Q getRequestValues() {
        return this.requestValues;
    }

    public TaskCallback<R> getTaskCallback() {
        return this.taskCallback;
    }

    public AbsTask setTaskCallback(TaskCallback<R> taskCallback) {
        this.taskCallback = taskCallback;
        return this;
    }

    public int getThreadType() {
        return this.threadType;
    }

    public AbsTask setThreadType(int threadType) {
        this.threadType = threadType;
        return this;
    }

    public long getDelayTime() {
        return this.delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    /** Completion callback of a task. */
    public static abstract class TaskCallback<R> {

        protected abstract void onError();

        protected abstract void onError(R response);

        protected abstract void onSuccess(R response);

        public void uiSuccess(final R response) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    TaskCallback.this.onSuccess(response);
                }
            });
        }

        public void uiError(final R response) {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    TaskCallback.this.onError(response);
                }
            });
        }

        public void uiError() {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    TaskCallback.this.onError();
                }
            });
        }
    }

    @Override
    public String toString() {
        return "AbsTask{Name=" + getClass().getSimpleName() + ", hashCode=" + hashCode()
                + ", taskState=" + taskStateToString(this.taskState) + ", threadType="
                + threadTypeToString(this.threadType) + ", delayTime=" + this.delayTime + '}';
    }

    private static String threadTypeToString(int threadType) {
        switch (threadType) {
            case ThreadType.NORMAL:
                return "NORMAL";
            case ThreadType.IO:
                return "IO";
            case ThreadType.SINGLE:
                return "SINGLE";
            case ThreadType.MAIN:
                return "MAIN";
            default:
                return "";
        }
    }

    private static String taskStateToString(java.util.concurrent.atomic.AtomicInteger state) {
        switch (state.get()) {
            case TaskState.NEW:
                return "NEW";
            case TaskState.DISPATCHED:
                return "DISPATCHED";
            case TaskState.WAITING:
                return "WAITING";
            case TaskState.RUNNING:
                return "RUNNING";
            case TaskState.COMPLETED:
                return "COMPLETED";
            default:
                return "";
        }
    }
}