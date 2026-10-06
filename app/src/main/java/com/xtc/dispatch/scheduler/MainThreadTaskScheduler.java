package com.xtc.dispatch.scheduler;

import android.util.Log;

import java.util.concurrent.Future;

/** Runs the tasks directly on the calling (main) thread. */
public class MainThreadTaskScheduler implements ITaskScheduler {

    private static final String TAG = "SingleTaskScheduler";

    private static MainThreadTaskScheduler instance;

    public static MainThreadTaskScheduler getInstance() {
        if (instance == null) {
            instance = new MainThreadTaskScheduler();
        }
        return instance;
    }

    private MainThreadTaskScheduler() {
        Log.d(TAG, "MainThreadTaskScheduler");
    }

    @Override
    public void execute(Runnable runnable) {
        runnable.run();
    }

    @Override
    public Future<?> submit(Runnable runnable) {
        runnable.run();
        return null;
    }
}