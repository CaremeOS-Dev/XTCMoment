package com.xtc.dispatch.task;

import android.util.Log;

import com.xtc.dispatch.sort.IDepend;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/** Tracks the lifecycle state of a task and its dependencies. */
public abstract class AbsTaskRun implements IDepend, ITask {

    protected static final String TAG = "AbsTask";

    /** Lifecycle states of a task. */
    public interface TaskState {
        int NEW = 1;
        int DISPATCHED = 2;
        int WAITING = 3;
        int RUNNING = 4;
        int COMPLETED = 5;
    }

    /** Thread pools a task can run on. */
    public interface ThreadType {
        int NORMAL = 1;
        int IO = 2;
        int SINGLE = 3;
        int MAIN = 4;
    }

    protected AtomicInteger taskState = new AtomicInteger(TaskState.NEW);
    protected int threadType = ThreadType.NORMAL;

    private final CountDownLatch depends;

    public AbsTaskRun() {
        this.depends = new CountDownLatch(dependsOn() == null ? 0 : dependsOn().size());
    }

    @Override
    public List<Class<? extends IDepend>> dependsOn() {
        return null;
    }

    @Override
    public void await() {
        try {
            this.depends.await();
        } catch (InterruptedException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void countDown() {
        this.depends.countDown();
    }

    public int getTaskState() {
        return this.taskState.get();
    }

    public int nextTaskState() {
        int state = this.taskState.incrementAndGet();
        if (state > TaskState.COMPLETED) {
            throw new RuntimeException("Task TaskState Exception：" + state);
        }
        Log.d(TAG, "nextTaskState value:" + state);
        return state;
    }
}