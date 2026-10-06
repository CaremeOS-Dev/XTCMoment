package com.xtc.moment.monitor;

import com.xtc.log.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Scheduler;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;

/**
 * IO 调度器与监控的全局管理器：可替换 RxJava 的 IO/计算/新线程调度器。
 */
public class IOMonitorManager {

    private String mPackageName;
    private List<String> mTargetNameList;
    private final ConcurrentHashMap<String, Boolean> mPilingMap;
    private AbstractScheduler mIOScheduler;
    private IThreadPool mIThreadPool;
    private boolean mIsRegisterRxJavaHook;
    private boolean mIsReplaceIOScheduler;
    private boolean mIsFilterStack;

    private static class SingleInstance {
        private static final IOMonitorManager INSTANCE = new IOMonitorManager();

        private SingleInstance() {
        }
    }

    public static IOMonitorManager getInstance() {
        return SingleInstance.INSTANCE;
    }

    private IOMonitorManager() {
        this.mPackageName = "default";
        this.mTargetNameList = new ArrayList(4);
        this.mPilingMap = new ConcurrentHashMap<>(4);
        this.mIOScheduler = new IOScheduler();
    }

    public String getPriorityName(int priority) {
        if (priority == IOTaskPriorityType.LOW_PRIORITY_TASK) {
            return "LOW_PRIORITY_TASK";
        }
        if (priority == IOTaskPriorityType.UPDATE_VIEW_TASK) {
            return "UPDATE_VIEW_TASK";
        }
        if (priority == IOTaskPriorityType.NORMAL_TASK) {
            return "NORMAL_TASK";
        }
        if (priority == IOTaskPriorityType.NETWORK_TASK) {
            return "NETWORK_TASK";
        }
        if (priority == IOTaskPriorityType.DATABASE_IO_TASK) {
            return "DATABASE_IO_TASK";
        }
        if (priority != IOTaskPriorityType.LOAD_DATA_FOR_VIEW_TASK) {
            return priority != IOTaskPriorityType.CORE_TASK ? "DEFAULT" : "CORE_TASK";
        }
        return "LOAD_DATA_FOR_VIEW_TASK";
    }

    public List<String> getTargetNameList() {
        List<String> targetNameList = this.mTargetNameList;
        return targetNameList == null ? new ArrayList() : targetNameList;
    }

    public String getPackageName() {
        String packageName = this.mPackageName;
        return packageName == null ? "" : packageName;
    }

    public boolean isRegisterRxJavaHook() {
        return this.mIsRegisterRxJavaHook;
    }

    IOMonitorManager setRegisterRxJavaHook(boolean registerRxJavaHook) {
        this.mIsRegisterRxJavaHook = registerRxJavaHook;
        return this;
    }

    public boolean isFilterStack() {
        return this.mIsFilterStack;
    }

    public Scheduler getIOScheduler() {
        AbstractScheduler scheduler = this.mIOScheduler;
        return scheduler == null ? new IOScheduler() : scheduler.create("", 0);
    }

    public IThreadPool getIThreadPool() {
        IThreadPool threadPool = this.mIThreadPool;
        if (threadPool != null) {
            return threadPool;
        }
        throw new NullPointerException(
                "开启监控前，请先初始化自定义IO scheduler的线程池，使用setReplaceIOScheduler和setIOThreadPool方法");
    }

    public static boolean isFilterStack(StackTraceElement element, boolean filterStack) {
        String className = element.getClassName();
        if (!filterStack) {
            return false;
        }
        if (className.startsWith("java.util.concurrent")
                || className.startsWith("java.lang.Thread")
                || className.startsWith("dalvik.system.VMStack")
                || className.startsWith("rx.internal")
                || className.startsWith("com.android.internal")
                || className.startsWith("java.lang.reflect.")
                || className.startsWith("android.app.ActivityThread")
                || className.startsWith("android.os")) {
            return true;
        }
        if ("rx.Observable".equals(className) && "subscribe".equals(element.getMethodName())) {
            return true;
        }
        return ("rx.Observable".equals(className) && "unsafeSubscribe".equals(element.getMethodName()))
                || className.startsWith("com.xtc.snmonitor.collector.monitor.thread");
    }

    public static boolean isFilterStack(StackTraceElement element) {
        return isFilterStack(element, getInstance().isFilterStack());
    }

    public boolean isReplaceIOScheduler() {
        return this.mIsReplaceIOScheduler;
    }

    private void replaceIOScheduler() {
        if (!isReplaceIOScheduler() || isRegisterRxJavaHook()) {
            return;
        }
        setRegisterRxJavaHook(true);
        RxJavaHooks.setOnComputationScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return IOMonitorManager.this.getIOScheduler();
            }
        });
        RxJavaHooks.setOnIOScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return IOMonitorManager.this.getIOScheduler();
            }
        });
        RxJavaHooks.setOnNewThreadScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return IOMonitorManager.this.getIOScheduler();
            }
        });
        LogUtil.d(IOMonitorConstants.MONITOR_LOG_TAG, ThreadPoolUtil.getSplitString("register rxjava hook for io scheduler."));
    }

    public IOMonitorManager setTargetNameList(List<String> targetNameList) {
        this.mTargetNameList = targetNameList;
        return this;
    }

    public IOMonitorManager setIOThreadPool(IThreadPool threadPool) {
        this.mIThreadPool = threadPool;
        replaceIOScheduler();
        return this;
    }

    public IOMonitorManager setReplaceIOScheduler(boolean replaceIOScheduler) {
        this.mIsReplaceIOScheduler = replaceIOScheduler;
        return this;
    }
}