package com.xtc.bigdata.monitor.anr;

import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.bigdata.collector.CollectionManager;
import com.xtc.bigdata.collector.encapsulation.entity.event.ExceptionEvent;
import com.xtc.bigdata.collector.utils.DateFormatUtil;
import com.xtc.bigdata.common.utils.FileUtils;
import com.xtc.log.LogUtil;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.module.Constants;

import java.io.File;
import java.lang.reflect.Method;

/** 主线程卡顿监控：监听 Looper 阻塞并把堆栈上报为异常事件。 */
@Deprecated
public class ANRMonitor {

    private static final String TAG = "ANRMonitor";
    private static final long ANR_DIR_MAX_SIZE = 10485760L;
    private static final int REASON_MAX_LENGTH = 1024;
    private static final int STACK_MAX_LENGTH = 10240;

    private Object cpuTrackerObj;
    private Method updateMethod;

    public void startMonitorMainThread() {
        startMonitor(Looper.getMainLooper(), true, Constants.DIFFER_TIME);
    }

    /** 开始监控指定 Looper。 */
    public void startMonitor(Looper looper, boolean ignoreDebugger, long timeout) {
        looper.setMessageLogging(new LooperMonitor(looper, ignoreDebugger, timeout,
                new LooperMonitor.BlockListener() {
                    @Override
                    public void onBlock(long blockTime, String stackTrace) {
                        try {
                            dumpBlockInfo(blockTime, stackTrace);
                        } catch (Throwable throwable) {
                            LogUtil.e(TAG, throwable);
                        }
                    }
                }));
        LogUtil.d(TAG, "start monitor thread:" + looper);
    }

    private void dumpBlockInfo(long blockTime, String stackTrace) throws Throwable {
        String cpuInfo = getCpuInfo();
        LogUtil.w(TAG, "blockTime:" + blockTime);
        LogUtil.w(TAG, "cpuInfo:" + cpuInfo);
        LogUtil.w(TAG, "stackTraceInfo:" + stackTrace);
        collectANRException(stackTrace);
    }

    private void collectANRException(String stackTrace) throws Throwable {
        if (TextUtils.isEmpty(stackTrace)) {
            return;
        }
        LogUtil.e(TAG, new ANRException(stackTrace));
        ExceptionEvent exceptionEvent = new ExceptionEvent();
        exceptionEvent.functionName = "BLOCK-ANR-ERROR-" + com.xtc.bigdata.common.constants.Constants.PACKAGE_NAME;
        exceptionEvent.dataCollectLevel = "B";
        exceptionEvent.dataSecurityLevel = "C";
        exceptionEvent.reason = stackTrace.substring(0,
                stackTrace.length() <= REASON_MAX_LENGTH ? stackTrace.length() : REASON_MAX_LENGTH);
        saveStackInfoAsFile(stackTrace);
        if (stackTrace.length() <= STACK_MAX_LENGTH) {
            exceptionEvent.stack = stackTrace;
        } else {
            exceptionEvent.stack = "stack info is larger than 1024 * 10,save as file";
        }
        CollectionManager.getInstance().exceptionEvent(exceptionEvent);
    }

    /** 清理过大或过期的 ANR 文件。 */
    private void clearExpireAnrFile() {
        File anrDir = new File(Environment.getExternalStorageDirectory().getPath() + "/bigData/anr/");
        File[] files = anrDir.exists() ? anrDir.listFiles() : null;
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            if (file.length() > ANR_DIR_MAX_SIZE) {
                FileUtils.deleteDir(file);
                LogUtil.i(TAG, "delete anr file when file is larger than 10MB:" + file);
            } else if (System.currentTimeMillis() - file.lastModified() >= ReminderHelper.ONE_WEEK) {
                FileUtils.deleteDir(file);
                LogUtil.i(TAG, "delete anr file:" + file);
            }
        }
    }

    private void saveStackInfoAsFile(String stackTrace) throws Throwable {
        clearExpireAnrFile();
        FileUtils.saveFile(Environment.getExternalStorageDirectory().getPath() + "/bigdata/anr/"
                + DateFormatUtil.format(DateFormatUtil.FORMAT_2, System.currentTimeMillis())
                + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER,
                stackTrace, System.currentTimeMillis() + ".txt");
    }

    /** 反射调用 ProcessCpuTracker 获取当前 CPU 状态。 */
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
            synchronized (ANRMonitor.class) {
                this.cpuTrackerObj.wait(500L);
            }
            this.updateMethod.invoke(this.cpuTrackerObj, new Object[0]);
            return (String) this.cpuTrackerObj.getClass().getMethod("printCurrentState", Long.TYPE)
                    .invoke(this.cpuTrackerObj, Long.valueOf(SystemClock.uptimeMillis()));
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return "";
        }
    }
}