package com.xtc.bigdata.monitor.anr;

import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.SystemClock;

import com.xtc.log.LogUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

/**
 * 主线程（及其它指定线程）卡顿看门狗。
 *
 * <p>周期性向被监控线程的 Handler 投递探测任务，若任务在超时时间内没有执行完，则判定该线程被阻塞，
 * 并输出阻塞描述与堆栈信息。</p>
 */
public class WatchDogs extends Thread {

    static final int COMPLETED = 0;
    static final int WAITING = 1;
    static final int WAITED_HALF = 2;
    static final int OVERDUE = 3;
    static final long DEFAULT_TIMEOUT = 4000L;
    static final String DLThreadName = "WatchDogs_DeadLockMonitorThread";
    static final String TAG = "WatchDogs";
    private static final int MAX_ACTIVE_SCAN_TASK = 10;

    private static final BlockListener DEFAULT_BLOCK_LISTENER = new BlockListener() {
        @Override
        public void onBlocked(String info) {
            LogUtil.e(TAG, "Block occurs ->\n" + info);
        }
    };

    static WatchDogs sWatchdog;

    private long MIN_TIMEOUT;
    private BlockListener blockListener;
    private boolean collectAllThreads;
    public boolean isInterrupted;
    private HandlerThread mDLThread;
    private ArrayList<HandlerChecker> mHandlerCheckers;
    private HandlerChecker mMonitorChecker;

    /** 卡顿回调。 */
    public interface BlockListener {
        void onBlocked(String info);
    }

    /** 被监控的执行体。 */
    public interface Monitor {
        void monitor();
    }

    public WatchDogs setBlockListener(BlockListener blockListener) {
        if (blockListener != null) {
            this.blockListener = blockListener;
        }
        return this;
    }

    /** 单个线程的探测任务。 */
    public final class HandlerChecker implements Runnable {

        private final Handler mHandler;
        private final String mName;
        private final long mTimeout;
        private final ArrayList<Monitor> mMonitors = new ArrayList<>();

        private Monitor mCurrentMonitor;
        private long mStartTime;
        private int realTid = -1;
        private boolean mCompleted = true;

        HandlerChecker(Handler handler, String name, long timeout) {
            this.mHandler = handler;
            this.mName = name;
            this.mTimeout = timeout;
        }

        public long getTimeout() {
            return this.mTimeout;
        }

        public void addMonitor(Monitor monitor) {
            this.mMonitors.add(monitor);
        }

        public void scheduleCheckLocked() {
            if (this.mCompleted) {
                this.mCompleted = false;
                this.mCurrentMonitor = null;
                this.mStartTime = SystemClock.uptimeMillis();
                this.mHandler.postAtFrontOfQueue(this);
            }
        }

        public boolean isOverdueLocked() {
            return !this.mCompleted && SystemClock.uptimeMillis() > this.mStartTime + this.mTimeout;
        }

        public void resetCompleted() {
            this.mCompleted = true;
        }

        public int getCompletionStateLocked() {
            if (this.mCompleted) {
                return COMPLETED;
            }
            long elapsed = SystemClock.uptimeMillis() - this.mStartTime;
            if (elapsed < this.mTimeout / 2) {
                return WAITING;
            }
            return elapsed < this.mTimeout ? WAITED_HALF : OVERDUE;
        }

        public Thread getThread() {
            return this.mHandler.getLooper().getThread();
        }

        public StackTraceElement[] getStackTrace() {
            return this.mHandler.getLooper().getThread().getStackTrace();
        }

        public String getName() {
            return this.mName;
        }

        public String describeBlockedStateLocked() {
            if (this.mCurrentMonitor == null) {
                return "Blocked in handler on " + this.mName + " (" + getThread().getName() + ") , realTid = "
                        + this.realTid;
            }
            return "Blocked in monitor " + this.mCurrentMonitor.getClass().getName() + " on " + this.mName + " ("
                    + getThread().getName() + ") , realTid = " + this.realTid;
        }

        @Override
        public void run() {
            if (this.realTid == -1) {
                this.realTid = Process.myTid();
            }
            int size = this.mMonitors.size();
            for (int index = 0; index < size; index++) {
                synchronized (WatchDogs.this) {
                    this.mCurrentMonitor = this.mMonitors.get(index);
                }
                this.mCurrentMonitor.monitor();
            }
            synchronized (WatchDogs.this) {
                this.mCompleted = true;
                this.mCurrentMonitor = null;
            }
        }

        @Override
        public String toString() {
            return "HandlerChecker{mName= " + this.mName + ", realTid= " + this.realTid + ", mTimeout= " + this.mTimeout
                    + ", mCompleted= " + this.mCompleted + ", mStartTime= " + this.mStartTime + ", nice= "
                    + Process.getThreadPriority(this.realTid) + " }";
        }
    }

    public static WatchDogs getInstance(int minTimeout) {
        if (sWatchdog == null) {
            synchronized (WatchDogs.class) {
                if (sWatchdog == null) {
                    sWatchdog = new WatchDogs(minTimeout);
                }
            }
        }
        return sWatchdog;
    }

    @Override
    public synchronized void start() {
        if (isAlive()) {
            LogUtil.w(TAG, "dumplicate start , return !");
        } else {
            LogUtil.d(TAG, "real start !");
            super.start();
        }
    }

    private WatchDogs(int minTimeout) {
        super(TAG);
        this.MIN_TIMEOUT = DEFAULT_TIMEOUT;
        this.blockListener = DEFAULT_BLOCK_LISTENER;
        this.mHandlerCheckers = new ArrayList<>();
        this.isInterrupted = false;
        this.collectAllThreads = false;
        if (minTimeout >= 200 && minTimeout < DEFAULT_TIMEOUT) {
            this.MIN_TIMEOUT = minTimeout;
        }
        LogUtil.i(TAG, "create a WatchDogs = " + toString());
        this.mHandlerCheckers.add(new HandlerChecker(new Handler(Looper.getMainLooper()), "main thread",
                this.MIN_TIMEOUT));
    }

    public WatchDogs addMonitor(Monitor monitor) {
        synchronized (this) {
            if (isAlive()) {
                throw new RuntimeException("Monitors can't be added once the Watchdog is running !!!");
            }
            if (this.mMonitorChecker != null) {
                this.mMonitorChecker.addMonitor(monitor);
            } else {
                LogUtil.w(TAG, "mMonitorChecker = null , pass");
            }
        }
        return this;
    }

    public WatchDogs addThread(Handler handler) {
        return addThread(handler, this.MIN_TIMEOUT);
    }

    public WatchDogs addThread(Handler handler, long timeout) {
        synchronized (this) {
            if (isAlive()) {
                throw new RuntimeException("Threads can't be added once the Watchdog is running");
            }
            this.mHandlerCheckers.add(new HandlerChecker(handler, handler.getLooper().getThread().getName(),
                    Math.max(timeout, this.MIN_TIMEOUT)));
        }
        return this;
    }

    private long getCheckInterval() {
        long interval = this.MIN_TIMEOUT / 2;
        for (int index = 0; index < this.mHandlerCheckers.size(); index++) {
            interval = Math.min(interval, this.mHandlerCheckers.get(index).getTimeout());
        }
        return interval;
    }

    private int evaluateCheckerCompletionLocked() {
        int state = COMPLETED;
        for (int index = 0; index < this.mHandlerCheckers.size(); index++) {
            state = Math.max(state, this.mHandlerCheckers.get(index).getCompletionStateLocked());
        }
        return state;
    }

    private ArrayList<HandlerChecker> getBlockedCheckersLocked() {
        ArrayList<HandlerChecker> blockedCheckers = new ArrayList<>();
        for (int index = 0; index < this.mHandlerCheckers.size(); index++) {
            HandlerChecker checker = this.mHandlerCheckers.get(index);
            if (checker.isOverdueLocked()) {
                blockedCheckers.add(checker);
                checker.resetCompleted();
            }
        }
        return blockedCheckers;
    }

    private StringBuilder describeCheckersLocked(ArrayList<HandlerChecker> blockedCheckers) {
        StringBuilder builder = new StringBuilder(128);
        for (int index = 0; index < blockedCheckers.size(); index++) {
            if (builder.length() > 0) {
                builder.append("\n");
            }
            builder.append(blockedCheckers.get(index).describeBlockedStateLocked());
        }
        return builder;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        LogUtil.i(TAG, "WatchDogs real run - " + toString());
        try {
            loopToCheck();
        } catch (Exception e) {
            LogUtil.e(TAG, "unknown Error , e = " + e);
            e.printStackTrace();
        }
    }

    private void loopToCheck() {
        while (!isInterrupted()) {
            checkBlock(false);
        }
        release();
    }

    @Override
    public boolean isInterrupted() {
        return this.isInterrupted;
    }

    private void checkBlock(boolean quiet) {
        long checkInterval = getCheckInterval();
        synchronized (this) {
            int debuggerState = 0;
            for (int index = 0; index < this.mHandlerCheckers.size(); index++) {
                this.mHandlerCheckers.get(index).scheduleCheckLocked();
            }
            long startTime = SystemClock.uptimeMillis();
            for (long remaining = checkInterval; remaining > 0;
                    remaining = checkInterval - (SystemClock.uptimeMillis() - startTime)) {
                if (Debug.isDebuggerConnected()) {
                    debuggerState = 2;
                }
                try {
                    wait(remaining);
                } catch (InterruptedException e) {
                    LogUtil.i(TAG, "thread has bean interrupted , e = " + e);
                }
                if (Debug.isDebuggerConnected()) {
                    debuggerState = 2;
                }
            }
            int completionState = evaluateCheckerCompletionLocked();
            if (completionState == COMPLETED) {
                return;
            }
            if (completionState == WAITING) {
                return;
            }
            if (completionState == WAITED_HALF) {
                if (!quiet) {
                    LogUtil.w(TAG, "some threads have waited over half time !");
                }
                return;
            }
            if (Debug.isDebuggerConnected()) {
                debuggerState = 2;
            }
            if (debuggerState > 0) {
                LogUtil.w(TAG, "Debugger connected or was connected , ignore !");
            } else {
                LogUtil.e(TAG, "now no debugger connected and block happen !");
                handleBlock(getBlockedCheckersLocked());
            }
        }
    }

    private void handleBlock(ArrayList<HandlerChecker> blockedCheckers) {
        StringBuilder info = describeCheckersLocked(blockedCheckers);
        info.append("\n");
        if (this.collectAllThreads) {
            info = getAllThreadStackTraces(info, blockedCheckers);
        } else {
            info = getJustBlockedStackTraces(info, blockedCheckers);
        }
        BlockListener listener = this.blockListener;
        if (listener != null) {
            listener.onBlocked(info.toString());
        }
    }

    private StringBuilder getAllThreadStackTraces(StringBuilder builder,
            ArrayList<HandlerChecker> blockedCheckers) {
        final Thread mainThread = Looper.getMainLooper().getThread();
        TreeMap<Thread, StackTraceElement[]> threadMap = new TreeMap<>(new Comparator<Thread>() {
            @Override
            public int compare(Thread first, Thread second) {
                if (first == second) {
                    return 0;
                }
                if (first == mainThread) {
                    return -1;
                }
                if (second == mainThread) {
                    return 1;
                }
                return second.getName().compareTo(first.getName());
            }
        });
        for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
            threadMap.put(entry.getKey(), entry.getValue());
        }
        if (!threadMap.containsKey(mainThread)) {
            threadMap.put(mainThread, mainThread.getStackTrace());
        }
        LogUtil.d(TAG, "current java thread count = " + threadMap.size());
        for (Map.Entry<Thread, StackTraceElement[]> entry : threadMap.entrySet()) {
            builder = getSingleThreadStateAndTrace(builder, entry.getKey(), entry.getValue());
        }
        return builder;
    }

    private StringBuilder getJustBlockedStackTraces(StringBuilder builder,
            ArrayList<HandlerChecker> blockedCheckers) {
        for (int index = 0; index < blockedCheckers.size(); index++) {
            HandlerChecker checker = blockedCheckers.get(index);
            builder = getSingleThreadStateAndTrace(builder, checker.getThread(), checker.getStackTrace());
        }
        return builder;
    }

    private StringBuilder getSingleThreadStateAndTrace(StringBuilder builder, Thread thread,
            StackTraceElement[] stackTrace) {
        builder.append("Thread = " + thread.getName() + " (state = " + thread.getState() + ")\n");
        for (StackTraceElement element : stackTrace) {
            builder.append("\t\tat ");
            builder.append(element.toString());
            builder.append("\n");
        }
        return builder;
    }

    public WatchDogs setCollectAllThread(boolean collectAllThreads) {
        this.collectAllThreads = collectAllThreads;
        return this;
    }

    public static void exit() {
        LogUtil.d(TAG, "receive commond exit -------");
        WatchDogs watchDogs = sWatchdog;
        if (watchDogs != null) {
            watchDogs.isInterrupted = true;
        }
    }

    private void release() {
        if (sWatchdog != null) {
            LogUtil.i(TAG, "release resources , sWatchdog = " + sWatchdog);
            HandlerThread handlerThread = this.mDLThread;
            if (handlerThread != null) {
                handlerThread.quit();
                this.mDLThread = null;
            }
            this.mHandlerCheckers.clear();
            this.mHandlerCheckers = null;
            this.mMonitorChecker = null;
            this.blockListener = null;
            sWatchdog = null;
        }
    }

    @Override
    public String toString() {
        int myTid = Process.myTid();
        return super.toString() + " , hashCode = " + hashCode() + " , (" + Process.myPid() + ScreenshotUtils.SEPARATOR
                + myTid + ") , nice = " + Process.getThreadPriority(myTid) + " , MIN_TIMEOUT = " + this.MIN_TIMEOUT;
    }
}