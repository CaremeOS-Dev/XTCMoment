package com.xtc.moment.monitor;

import com.xtc.log.LogUtil;
import com.xtc.moment.util.SystemUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Scheduler;

/**
 * IO 调度器监控工具：按机器等级决定是否打印调度堆栈与线程池状态。
 */
public class MonitorUtil {

    private static final int ERROR_LIMIT = 5;
    private static final int WARN_LIMIT = 2;

    private static final AtomicInteger sWorkerNum = new AtomicInteger(0);
    private static final boolean IS_MONITOR = SystemUtil.isHighMachine();

    private static int sMonitorState = 0;

    static boolean isUpload = false;

    interface MonitorState {
        int WARN = 1;
        int ERROR = 2;
    }

    public static Scheduler.Worker createNewWorker(String taskName, int priority) {
        if (!isOpenMonitorLog()) {
            return new ExecutorSchedulerWorker(
                    IOMonitorManager.getInstance().getIThreadPool().getMainIOThreadPoolExecutor(), taskName, priority);
        }
        int workerNum = sWorkerNum.getAndIncrement();
        printStack("IOScheduler", "createNewWorker : " + workerNum);
        return new ExecutorSchedulerWorker(
                IOMonitorManager.getInstance().getIThreadPool().getMainIOThreadPoolExecutor(),
                taskName + " " + workerNum, priority);
    }

    public static void logBeforeExecute(String tag, Thread thread, Runnable runnable) {
        if (isOpenMonitorLog() && (runnable instanceof ExecutorSchedulerWorker)) {
            d(tag, "beforeExecute: TaskName", ((ExecutorSchedulerWorker) runnable).getTaskName());
        }
    }

    public static void logThreadPoolExecute(String tag, ThreadPoolExecutor executor) {
        if (isOpenMonitorLog()) {
            d(tag, "execute MonitorThreadPoolExecutor State ", getStateStr(executor));
        }
    }

    private static String getStateStr(ThreadPoolExecutor executor) {
        return " isTerminated = " + executor.isTerminated()
                + ",isShutdown = " + executor.isShutdown()
                + ",isTerminating = " + executor.isTerminating()
                + ",getActiveCount = " + executor.getActiveCount()
                + ",getCompletedTaskCount = " + executor.getCompletedTaskCount()
                + ",QueueSize = " + executor.getQueue().size()
                + ",getTaskCount = " + executor.getTaskCount() + "";
    }

    public static void d(String tag, String message, Object value) {
        if (isOpenMonitorLog()) {
            LogUtil.d(getMonitorTag(tag), message + " = [" + value + "]");
        }
    }

    private static boolean isOpenMonitorLog() {
        return IS_MONITOR && sMonitorState >= 1;
    }

    private static void printStack(String tag, String message) {
        LogUtil.e(getMonitorTag(tag), message, new NullPointerException());
    }

    private static String getMonitorTag(String tag) {
        return IOMonitorConstants.MONITOR_TAG + tag;
    }
}