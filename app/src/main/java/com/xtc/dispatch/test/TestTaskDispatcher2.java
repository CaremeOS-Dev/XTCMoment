package com.xtc.dispatch.test;

import android.content.Context;
import android.util.Log;

import com.xtc.dispatch.TaskDispatcher;
import com.xtc.dispatch.sort.IDepend;
import com.xtc.dispatch.task.AbsTask;

import java.util.ArrayList;
import java.util.List;

/** Manual test of {@link TaskDispatcher} using mixed thread types. */
public class TestTaskDispatcher2 {

    private static final String TAG = "TestTaskDispatcher2";

    /** Task depending on {@link TaskB} and {@link TaskC}. */
    public static class TaskA extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
            Log.d(TAG, "TaskA executeTask:" + Thread.currentThread());
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
            Log.d(TAG, "TaskB executeTask:" + Thread.currentThread());
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
            Log.d(TAG, "TaskC executeTask:" + Thread.currentThread());
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskD.class);
            return dependencies;
        }
    }

    /** Leaf task without dependencies. */
    public static class TaskD extends AbsTask {
        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            return null;
        }

        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
            Log.d(TAG, "TaskD executeTask:" + Thread.currentThread());
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
            Log.d(TAG, "TaskE executeTask:" + Thread.currentThread());
        }
    }

    /** Task depending on {@link TaskA} and {@link TaskD}. */
    public static class TaskF extends AbsTask {
        @Override
        protected void executeTask(AbsTask.RequestValues requestValues) {
            Log.d(TAG, "TaskF executeTask:" + Thread.currentThread());
        }

        @Override
        public List<Class<? extends IDepend>> dependsOn() {
            ArrayList<Class<? extends IDepend>> dependencies = new ArrayList<>();
            dependencies.add(TaskA.class);
            dependencies.add(TaskD.class);
            return dependencies;
        }
    }

    /** Adds every test task to the dispatcher with mixed thread types and dispatches them. */
    public static void start(Context context) {
        TaskDispatcher dispatcher = TaskDispatcher.create(context);
        dispatcher.addTask(new TaskA().setThreadType(2));
        dispatcher.addTask(new TaskB().setThreadType(1));
        dispatcher.addTask(new TaskC().setThreadType(2));
        dispatcher.addTask(new TaskD().setThreadType(4));
        dispatcher.addTask(new TaskE().setThreadType(3));
        dispatcher.addTask(new TaskF().setThreadType(1));
        dispatcher.dispatch();
    }
}