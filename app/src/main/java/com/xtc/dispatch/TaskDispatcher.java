package com.xtc.dispatch;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.xtc.dispatch.scheduler.SchedulerHandler;
import com.xtc.dispatch.sort.DependSortUtil;
import com.xtc.dispatch.sort.IDepend;
import com.xtc.dispatch.task.AbsTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Collects the startup tasks, sorts them by their declared dependencies and dispatches
 * them onto the matching thread pools.
 */
public class TaskDispatcher {

    private static final String TAG = "TaskDispatcher";

    private final Context context;
    private List<AbsTask> tasks = new ArrayList<>();
    private final List<Class<? extends IDepend>> taskTypes = new ArrayList<>();
    private final HashMap<Class<? extends IDepend>, ArrayList<AbsTask>> dependentTasks = new HashMap<>();
    private final List<Future> futures = new ArrayList<>();
    private final List<Class<? extends AbsTask>> dispatchedTaskTypes = new ArrayList<>(100);

    public static TaskDispatcher create(Context context) {
        return new TaskDispatcher(context);
    }

    private TaskDispatcher(Context context) {
        this.context = context;
    }

    /** Adds [task] and records its dependencies. */
    public TaskDispatcher addTask(AbsTask task) {
        if (task != null) {
            collectDepends(task);
            this.tasks.add(task);
            this.taskTypes.add((Class<? extends IDepend>) task.getClass());
        }
        return this;
    }

    private void collectDepends(AbsTask task) {
        if (task.dependsOn() == null || task.dependsOn().size() <= 0) {
            return;
        }
        if (task.getThreadType() == AbsTask.ThreadType.MAIN) {
            throw new RuntimeException("Main Thread Task Can't dependsOn Other Tasks !");
        }
        for (Class<? extends IDepend> dependsOn : task.dependsOn()) {
            Log.d(TAG, "collectDepends cls:" + dependsOn);
            if (this.dependentTasks.get(dependsOn) == null) {
                this.dependentTasks.put(dependsOn, new ArrayList<AbsTask>());
            }
            this.dependentTasks.get(dependsOn).add(task);
            if (this.dispatchedTaskTypes.contains(dependsOn)) {
                task.countDown();
            }
        }
    }

    /** Sorts and dispatches every collected task. Must be called from the main thread. */
    public void dispatch() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("Must be called from MainThread");
        }
        if (this.tasks.size() > 0) {
            this.tasks = DependSortUtil.sort(this.tasks, this.taskTypes);
            dispatchTasks();
        }
    }

    /** Cancels every dispatched task. */
    public void cancelAll() {
        for (Future future : this.futures) {
            if (future != null) {
                future.cancel(true);
            }
        }
    }

    /** Records that [task] has been dispatched. */
    public void markTaskDispatched(AbsTask task) {
        this.dispatchedTaskTypes.add((Class<? extends AbsTask>) task.getClass());
    }

    private void dispatchTasks() {
        for (AbsTask task : this.tasks) {
            Log.d(TAG, "dispatchTask task:" + task);
            task.nextTaskState();
            this.futures.add(SchedulerHandler.scheduleFuture(task, this));
        }
    }

    /** Releases the tasks that depend on [task]. */
    public void onTaskCompleted(AbsTask task) {
        Log.d(TAG, "countDownChildren preTask:" + task.getClass());
        ArrayList<AbsTask> children = this.dependentTasks.get(task.getClass());
        if (children != null && children.size() > 0) {
            Iterator<AbsTask> iterator = children.iterator();
            while (iterator.hasNext()) {
                iterator.next().countDown();
            }
        }
        this.dependentTasks.remove(task);
    }

    /** Runs [task] immediately, bypassing the dependency sort. */
    public static void dispatchImmediately(AbsTask task) {
        task.nextTaskState();
        SchedulerHandler.scheduleFuture(task, null);
    }
}