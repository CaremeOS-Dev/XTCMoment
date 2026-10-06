package com.xtc.bigdata.monitor.anr;

import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;

import com.xtc.bigdata.collector.exception.CrashHandler;
import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;

import java.lang.reflect.Method;

/** 独立线程的 ANR 看门狗：定时向主线程投递探测任务，超时未执行即视为卡顿。 */
@Deprecated
public class ANRWatchDog extends Thread {

    private static final String TAG = "ANRWatchDog";
    private static final int THREAD_PRIORITY = 10;
    private static final long CPU_TRACKER_WAIT = 500L;
    private static final int STACK_MAX_LENGTH = 102400;

    /** 卡顿回调。 */
    public interface AnrListener {
        void onAnrHappened(String stackTrace);
    }

    private AnrChecker anrChecker;
    private AnrListener anrListener;
    private Object cpuTrackerObj;
    private boolean ignoreDebugger;
    private Handler mainHandler;
    private int timeout;
    private Method updateMethod;

    private class AnrChecker implements Runnable {
        private long executeTime;
        private boolean mCompleted;
        private long mStartTime;

        AnrChecker() {
            this.executeTime = SystemClock.uptimeMillis();
        }

        @Override
        public void run() {
            synchronized (ANRWatchDog.this) {
                this.mCompleted = true;
                this.executeTime = SystemClock.uptimeMillis();
            }
        }

        void schedule() {
            this.mCompleted = false;
            this.mStartTime = SystemClock.uptimeMillis();
            mainHandler.postAtFrontOfQueue(this);
        }

        boolean isBlocked() {
            return !this.mCompleted || this.executeTime - this.mStartTime >= Constants.DIFFER_TIME;
        }
    }

    private ANRWatchDog(Builder builder) {
        super("ANR-WatchDog-Thread");
        this.timeout = CrashHandler.REPORT_DURATION;
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.anrChecker = new AnrChecker();
        this.timeout = builder.timeout;
        this.ignoreDebugger = builder.ignoreDebugger;
        this.anrListener = builder.anrListener;
    }

    @Override
    public void run() {
        LogUtil.d(TAG, "start watch anr...");
        Process.setThreadPriority(THREAD_PRIORITY);
        while (!isInterrupted()) {
            synchronized (this) {
                this.anrChecker.schedule();
                long remaining = this.timeout;
                long startTime = SystemClock.uptimeMillis();
                while (remaining > 0) {
                    try {
                        wait(remaining);
                    } catch (InterruptedException e) {
                        LogUtil.w(TAG, e.toString());
                    }
                    remaining = this.timeout - (SystemClock.uptimeMillis() - startTime);
                }
                if (this.anrChecker.isBlocked()) {
                    if (!this.ignoreDebugger && Debug.isDebuggerConnected()) {
                        LogUtil.w(TAG, "device is debugging...");
                    } else {
                        String cpuInfo = getCpuInfo();
                        String stackTraceInfo = getStackTraceInfo();
                        LogUtil.w(TAG, "cpuInfo:" + cpuInfo);
                        LogUtil.w(TAG, "stackTraceInfo:" + stackTraceInfo);
                        AnrListener listener = this.anrListener;
                        if (listener != null) {
                            listener.onAnrHappened(stackTraceInfo);
                        }
                    }
                }
            }
        }
        LogUtil.w(TAG, getName() + " thread is interrupted");
    }

    private String getCpuInfo() {
        try {
            if (this.cpuTrackerObj == null) {
                this.cpuTrackerObj = Class.forName("com.android.internal.os.ProcessCpuTracker")
                        .getConstructor(Boolean.TYPE).newInstance(Boolean.valueOf(false));
                this.cpuTrackerObj.getClass().getMethod("init", new Class[0])
                        .invoke(this.cpuTrackerObj, new Object[0]);
                this.updateMethod = this.cpuTrackerObj.getClass().getMethod("update", new Class[0]);
            } else {
                this.updateMethod.invoke(this.cpuTrackerObj, new Object[0]);
            }
            synchronized (ANRWatchDog.class) {
                this.cpuTrackerObj.wait(CPU_TRACKER_WAIT);
            }
            this.updateMethod.invoke(this.cpuTrackerObj, new Object[0]);
            return (String) this.cpuTrackerObj.getClass().getMethod("printCurrentState", Long.TYPE)
                    .invoke(this.cpuTrackerObj, Long.valueOf(SystemClock.uptimeMillis()));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return "";
        }
    }

    private String getStackTraceInfo() {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : Looper.getMainLooper().getThread().getStackTrace()) {
            builder.append(element.toString());
            builder.append("\r\n");
        }
        return builder.substring(0, builder.length() < STACK_MAX_LENGTH ? builder.length() : STACK_MAX_LENGTH);
    }

    /** ANRWatchDog 构造器。 */
    public static class Builder {
        AnrListener anrListener;
        boolean ignoreDebugger;
        int timeout;

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder ignoreDebugger(boolean ignoreDebugger) {
            this.ignoreDebugger = ignoreDebugger;
            return this;
        }

        public Builder anrListener(AnrListener anrListener) {
            this.anrListener = anrListener;
            return this;
        }

        public ANRWatchDog build() {
            return new ANRWatchDog(this);
        }
    }
}