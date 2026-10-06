package com.xtc.bigdata.monitor.performance;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.os.SystemClock;

import com.xtc.bigdata.collector.CollectionManager;
import com.xtc.bigdata.collector.encapsulation.entity.event.CustomEvent;
import com.xtc.bigdata.common.utils.ResourceUsedUtil;
import com.xtc.log.LogUtil;
import com.xtc.virtualselfapi.constants.Constants;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/** 性能监控：定时采集进程 CPU / 内存占用并上报自定义事件。 */
public class PerfMonitor {

    private static final String SCENE_NORMAL = "normal";
    private static final String SCENE_SCREEN_OFF = "screenOff";
    private static final String SCENE_SCREEN_ON = "screenOn";
    private static final String TAG = "PerfMonitor";
    private static final long CPU_TRACKER_WAIT = 500L;
    private static volatile int collectInterval = 20000;

    private static PerfMonitor monitor;

    private Context applicationContext;
    private Handler handler;
    private HandlerThread handlerThread;
    private long lastCollectBeginTimeE;
    private int myPid;
    private Object processCpuTracker;
    private volatile boolean looperStarted = false;

    private Runnable normalMonitorRunnable = new Runnable() {
        @Override
        public void run() {
            if (SystemClock.elapsedRealtime() - lastCollectBeginTimeE >= collectInterval) {
                collectInfo(SCENE_NORMAL);
            }
            if (handler == null || !looperStarted) {
                return;
            }
            handler.postDelayed(normalMonitorRunnable, collectInterval);
        }
    };

    private Runnable screenOnRunnable = new Runnable() {
        @Override
        public void run() {
            collectInfo(SCENE_SCREEN_ON);
        }
    };

    private Runnable screenOffRunnable = new Runnable() {
        @Override
        public void run() {
            collectInfo(SCENE_SCREEN_OFF);
        }
    };

    public static synchronized PerfMonitor getInstance() {
        if (monitor == null) {
            monitor = new PerfMonitor();
        }
        LogUtil.d(TAG, "monitor = " + monitor);
        return monitor;
    }

    /** 启动定时采集。 */
    public synchronized void startLooper(Context context) {
        if (context == null) {
            LogUtil.w(TAG, "ctx != null , return !");
            return;
        }
        if (this.looperStarted) {
            return;
        }
        this.myPid = Process.myPid();
        if (this.handler == null) {
            this.handlerThread = new HandlerThread(TAG);
            this.handlerThread.start();
            this.handler = new Handler(this.handlerThread.getLooper());
        }
        stopInner();
        realStart(context);
    }

    private void realStart(Context context) {
        this.applicationContext = context.getApplicationContext();
        this.handler.postDelayed(this.normalMonitorRunnable, Constants.DEFAULT_INIT_DELAY_TIME);
        this.looperStarted = true;
        LogUtil.d(TAG, "PerfMonitor looperStarted ---");
    }

    /** 亮屏时采集一次。 */
    public static synchronized void collectScreenOn() {
        if (monitor != null) {
            if (monitor.handler != null) {
                monitor.handler.post(monitor.screenOnRunnable);
            }
        } else {
            LogUtil.i(TAG, "monitor = null , just return !");
        }
    }

    /** 灭屏时采集一次。 */
    public static synchronized void collectScreenOff() {
        if (monitor != null) {
            if (monitor.handler != null) {
                monitor.handler.post(monitor.screenOffRunnable);
            }
        } else {
            LogUtil.i(TAG, "monitor = null , just return !");
        }
    }

    public static void setCollectInterval(int interval) {
        collectInterval = interval;
    }

    private synchronized void stopInner() {
        if (this.looperStarted) {
            if (this.handler != null) {
                this.handler.removeCallbacks(this.normalMonitorRunnable);
            }
            this.looperStarted = false;
            LogUtil.d(TAG, "monitor stopped ---");
        }
    }

    public static void stopLooper() {
        PerfMonitor perfMonitor = monitor;
        if (perfMonitor != null) {
            perfMonitor.stopInner();
        } else {
            LogUtil.i(TAG, "monitor = null , just return !");
        }
    }

    /** 退出并释放线程。 */
    public static void quit() {
        PerfMonitor perfMonitor = monitor;
        if (perfMonitor == null) {
            LogUtil.i(TAG, "monitor = null , just return !");
            return;
        }
        perfMonitor.stopInner();
        Handler handler = monitor.handler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        HandlerThread handlerThread = monitor.handlerThread;
        if (handlerThread != null) {
            handlerThread.quit();
        }
        PerfMonitor current = monitor;
        current.handlerThread = null;
        current.handler = null;
        monitor = null;
        LogUtil.d(TAG, "monitor quitted ---");
    }

    /** 采集一次 CPU / 内存数据并上报。 */
    private void collectInfo(String scene) {
        this.lastCollectBeginTimeE = SystemClock.elapsedRealtime();
        long collectStartTime = System.currentTimeMillis();
        try {
            Class<?> cpuTrackerClass = Class.forName("com.android.internal.os.ProcessCpuTracker");
            Constructor<?> constructor = cpuTrackerClass.getConstructor(Boolean.TYPE);
            constructor.setAccessible(true);
            Method initMethod = cpuTrackerClass.getDeclaredMethod("init", new Class[0]);
            initMethod.setAccessible(true);
            Method updateMethod = cpuTrackerClass.getDeclaredMethod("update", new Class[0]);
            updateMethod.setAccessible(true);
            Method cpuTimeForPidMethod = cpuTrackerClass.getDeclaredMethod("getCpuTimeForPid", Integer.TYPE);
            cpuTimeForPidMethod.setAccessible(true);
            Method lastUserTimeMethod = cpuTrackerClass.getDeclaredMethod("getLastUserTime", new Class[0]);
            lastUserTimeMethod.setAccessible(true);
            Method lastSystemTimeMethod = cpuTrackerClass.getDeclaredMethod("getLastSystemTime", new Class[0]);
            lastSystemTimeMethod.setAccessible(true);
            Method lastIoWaitTimeMethod = cpuTrackerClass.getDeclaredMethod("getLastIoWaitTime", new Class[0]);
            lastIoWaitTimeMethod.setAccessible(true);
            Method lastIrqTimeMethod = cpuTrackerClass.getDeclaredMethod("getLastIrqTime", new Class[0]);
            lastIrqTimeMethod.setAccessible(true);
            Method lastSoftIrqTimeMethod = cpuTrackerClass.getDeclaredMethod("getLastSoftIrqTime", new Class[0]);
            lastSoftIrqTimeMethod.setAccessible(true);
            Method lastIdleTimeMethod = cpuTrackerClass.getDeclaredMethod("getLastIdleTime", new Class[0]);
            lastIdleTimeMethod.setAccessible(true);
            Field lastSampleWallTimeField = cpuTrackerClass.getDeclaredField("mLastSampleWallTime");
            lastSampleWallTimeField.setAccessible(true);
            Field currentSampleWallTimeField = cpuTrackerClass.getDeclaredField("mCurrentSampleWallTime");
            currentSampleWallTimeField.setAccessible(true);
            if (this.processCpuTracker == null) {
                this.processCpuTracker = constructor.newInstance(Boolean.valueOf(true));
            }
            initMethod.invoke(this.processCpuTracker, new Object[0]);
            updateMethod.invoke(this.processCpuTracker, new Object[0]);
            long startCpuTime = ((Long) cpuTimeForPidMethod.invoke(this.processCpuTracker,
                    Integer.valueOf(this.myPid))).longValue();
            synchronized (PerfMonitor.class) {
                this.processCpuTracker.wait(CPU_TRACKER_WAIT);
            }
            updateMethod.invoke(this.processCpuTracker, new Object[0]);
            long lastSampleWallTime = ((Long) lastSampleWallTimeField.get(this.processCpuTracker)).longValue();
            long currentSampleWallTime = ((Long) currentSampleWallTimeField.get(this.processCpuTracker)).longValue();
            long endCpuTime = ((Long) cpuTimeForPidMethod.invoke(this.processCpuTracker,
                    Integer.valueOf(this.myPid))).longValue();
            int userTime = ((Integer) lastUserTimeMethod.invoke(this.processCpuTracker, new Object[0])).intValue();
            int systemTime = ((Integer) lastSystemTimeMethod.invoke(this.processCpuTracker, new Object[0]))
                    .intValue();
            int idleTime = ((Integer) lastIdleTimeMethod.invoke(this.processCpuTracker, new Object[0])).intValue();
            long totalCpuTime = userTime + systemTime + idleTime
                    + ((Integer) lastIoWaitTimeMethod.invoke(this.processCpuTracker, new Object[0])).intValue()
                    + ((Integer) lastIrqTimeMethod.invoke(this.processCpuTracker, new Object[0])).intValue()
                    + ((Integer) lastSoftIrqTimeMethod.invoke(this.processCpuTracker, new Object[0])).intValue();
            float totalCpuUsed = save2BitDecimal(((totalCpuTime - idleTime) * 100.0f) / (float) totalCpuTime);
            float totalProcUsed = save2BitDecimal(((endCpuTime - startCpuTime) * 100.0f) / (float) totalCpuTime);
            long memoryStartTime = System.currentTimeMillis();
            int[] memoryUsed = ResourceUsedUtil.getMemoryUsedOfProc(this.myPid, this.applicationContext);
            float nowPss = save2BitDecimal((memoryUsed[0] * 1.0f) / 1024.0f);
            float nowUss = save2BitDecimal((memoryUsed[1] * 1.0f) / 1024.0f);
            long memoryCollectUsedTime = System.currentTimeMillis() - memoryStartTime;
            long totalCollectUsedTime = System.currentTimeMillis() - collectStartTime;
            CustomEvent customEvent = new CustomEvent();
            HashMap<String, String> extend = new HashMap<>();
            customEvent.functionName = TAG;
            extend.put("hasBootedTime", this.lastCollectBeginTimeE + "");
            extend.put("scene", scene);
            extend.put("collectInterval", collectInterval + "");
            extend.put("myPid", this.myPid + "");
            extend.put("startCollectTime", lastSampleWallTime + "");
            extend.put("endCollectTime", currentSampleWallTime + "");
            extend.put("totalCpuTime", totalCpuTime + "");
            extend.put("totalCpuUsed", totalCpuUsed + "");
            extend.put("totalProcUsed", totalProcUsed + "");
            extend.put("nowPss", nowPss + "");
            extend.put("nowUss", nowUss + "");
            extend.put("memCollectUsedTime", memoryCollectUsedTime + "");
            extend.put("totalCollectUsedTime", totalCollectUsedTime + "");
            customEvent.setExtend(extend);
            CollectionManager.getInstance().customEvent(customEvent);
        } catch (Exception e) {
            LogUtil.e(TAG, "e = " + e);
            e.printStackTrace();
        }
    }

    private float save2BitDecimal(float value) {
        if (value <= 0.0f) {
            value = 0.0f;
        }
        return Math.round(value * 100.0f) / 100.0f;
    }

    private void splitStringToGetData(String content) {
        boolean found = false;
        for (String line : content.split("\n")) {
            if (!line.contains("%")) {
                continue;
            }
            if (!found && line.contains(com.xtc.moment.module.Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER)) {
                int pidStart = line.indexOf("%") + 1;
                int pidEnd = line.indexOf(com.xtc.moment.module.Constants.ProviderConstants
                        .SEPARATOR_CONTENT_PROVIDER);
                if (this.myPid == Integer.parseInt(line.substring(pidStart, pidEnd).trim())) {
                    LogUtil.d(TAG, "array = " + line + " , procs = " + line.substring(0, line.indexOf("%") + 1).trim());
                    found = true;
                }
            }
            if (line.contains("TOTAL")) {
                LogUtil.d(TAG, "totals = " + line.substring(0, line.indexOf("TOTAL") - 1).trim());
            }
        }
    }
}