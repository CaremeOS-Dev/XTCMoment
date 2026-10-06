package com.xtc.dispatch.test;

import android.content.Context;
import android.util.Log;

import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.sort.IDepend;
import com.xtc.dispatch.task.AbsTask;

import java.util.ArrayList;
import java.util.List;

/** Manual test of {@link TaskDispatcher} with a small dependency graph. */
public class TestTaskDispatcher {

    private static final String TAG = "TestTaskDispatcher";

    /** Task depending on {@link TaskB} and {@link TaskC}. */
    public static class TaskA extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
            Log.d(TAG, "TaskA executeTask");
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
            Log.d(TAG, "TaskB executeTask");
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
            Log.d(TAG, "TaskC executeTask");
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
            Log.d(TAG, "TaskD executeTask");
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskE.class);
            return dependencies;
        }
    }

    /** Leaf task without dependencies. */
    public static class TaskE extends AbsTask {
        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            return null;
        }

        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
            Log.d(TAG, "TaskE executeTask");
        }
    }

    /** Task depending on {@link TaskA}. */
    public static class TaskF extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
            Log.d(TAG, "TaskF executeTask");
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskA.class);
            return dependencies;
        }
    }

    /** Adds every test task to the dispatcher and dispatches them. */
    public static void start(Context context) {
        TaskDispatcher dispatcher = TaskDispatcher.create(context);
        dispatcher.addTask(new TaskA().setThreadType(1));
        dispatcher.addTask(new TaskB().setThreadType(1));
        dispatcher.addTask(new TaskC().setThreadType(1));
        dispatcher.addTask(new TaskD().setThreadType(1));
        dispatcher.addTask(new TaskE().setThreadType(1));
        dispatcher.addTask(new TaskF().setThreadType(1));
        dispatcher.dispatch();
    }
}