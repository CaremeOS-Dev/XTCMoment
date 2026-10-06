package com.xtc.bigdata.common.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/** Shared executors for the big-data library. */
public class ExecutorProvider {

    public static Executor main() {
        return MainExecutorHolder.INSTANCE;
    }

    public static Executor background() {
        return BackgroundExecutorHolder.INSTANCE;
    }

    private static class MainExecutor implements Executor {
        private final Handler handler;

        private MainExecutor() {
            this.handler = new Handler(Looper.getMainLooper());
        }

        @Override
        public void execute(Runnable runnable) {
            this.handler.post(runnable);
        }
    }

    private static class MainExecutorHolder {
        private static final Executor INSTANCE = new MainExecutor();

        private MainExecutorHolder() {
        }
    }

    private static class BackgroundExecutorHolder {
        private static final Executor INSTANCE = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable, "BigdataClient");
            }
        });

        private BackgroundExecutorHolder() {
        }
    }

    private ExecutorProvider() {
    }
}