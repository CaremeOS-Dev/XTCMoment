package com.xtc.dispatch.test;

import android.util.Log;

import com.xtc.dispatch.sort.DependSortUtil;
import com.xtc.dispatch.sort.IDepend;
import com.xtc.dispatch.task.AbsTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Manual test of {@link DependSortUtil} with a small dependency graph. */
public class TestTaskSort {

    private static final String TAG = "TestTaskSort";

    /** Leaf task without dependencies. */
    public static class TaskE extends AbsTask {
        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            return null;
        }

        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
        }
    }

    /** Task depending on {@link TaskB} and {@link TaskC}. */
    public static class TaskA extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskB.class);
            dependencies.add(TaskC.class);
            return dependencies;
        }
    }

    /** Task depending on {@link TaskC} and {@link TaskD}. */
    public static class TaskB extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskC.class);
            dependencies.add(TaskD.class);
            return dependencies;
        }
    }

    /** Task depending on {@link TaskD}. */
    public static class TaskC extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskD.class);
            return dependencies;
        }
    }

    /** Task depending on {@link TaskE}. */
    public static class TaskD extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskE.class);
            return dependencies;
        }
    }

    /** Task depending on {@link TaskA}. */
    public static class TaskF extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskA.class);
            return dependencies;
        }
    }

    /** Registers every task and logs the resulting topological order. */
    public static void start() {
        List<AbsTask> tasks = new ArrayList<>();
        List<Class<? extends IDepend>> taskTypes = new ArrayList<>();
        register(tasks, taskTypes, new TaskA());
        register(tasks, taskTypes, new TaskB());
        register(tasks, taskTypes, new TaskC());
        register(tasks, taskTypes, new TaskD());
        register(tasks, taskTypes, new TaskE());
        register(tasks, taskTypes, new TaskF());
        Iterator<IDepend> iterator = DependSortUtil.sort(tasks, taskTypes).iterator();
        while (iterator.hasNext()) {
            Log.d(TAG, "task:" + iterator.next().getClass());
        }
    }

    /** Adds the task and its type to the parallel lists. */
    private static void register(List<AbsTask> tasks, List<Class<? extends IDepend>> taskTypes, AbsTask task) {
        tasks.add(task);
        taskTypes.add(task.getClass());
    }
}