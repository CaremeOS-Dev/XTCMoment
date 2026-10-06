package com.xtc.bigdata.collector.exception;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.load.Key;
import com.xtc.bigdata.collector.ShareHelper;
import com.xtc.bigdata.collector.config.ConfigAgent;
import com.xtc.bigdata.collector.config.DeviceInfo;
import com.xtc.bigdata.collector.config.OdexConfig;
import com.xtc.bigdata.collector.encapsulation.entity.event.ExceptionEvent;
import com.xtc.bigdata.collector.utils.DateFormatUtil;
import com.xtc.bigdata.collector.utils.MD5Coder;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.constants.EType;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.FileUtils;
import com.xtc.bigdata.common.utils.SharedPrefUtils;
import com.xtc.bigdata.common.utils.StoreUtils;
import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.SystemPropertyUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;

/** Global uncaught-exception handler that records crashes for the collector. */
public final class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String CRASH_REPORTER_EXTENSION = ".cr";

    /** Maximum number of crash reports kept per day. */
    public static final int DAY_MAX = 100;

    private static final long DEFAULT_CRASH_CACHE_SIZE = 1048576L;

    public static final String FUNCTION_ODEX_OPT = "odex_opt";

    public static final long HOUR = 3600000L;

    public static final int MAX_CRASH_REPORT_DURATION = 3600000;

    public static final int REPORT_DURATION = 5000;

    private static final int RESTART_COUNT = 5;

    private static final String STACK_TRACE = "STACK_TRACE";

    private static final String STACK_TRACE_SEPARATOR = "=";

    private static final String TAG = "CrashHandler";

    private static final boolean DEBUG = Constants.isDebug;

    private static final String SDCARD_PATH = "/mnt/sdcard";
    private static final String DEFAULT_CRASH_LOG_PATH = SDCARD_PATH + File.separator + ".crash" + File.separator;

    private static final String KEY_FIRST_CRASH_TIME = "first_crash_time";
    private static final String KEY_LAST_CRASH_TIME = "last_crash_time";
    private static final String KEY_CRASH_COUNT = "crash_count";

    private static CrashHandler instance;
    private static OdexConfig odexConfig;
    private static boolean odexDealSwitch;

    private Thread.UncaughtExceptionHandler defaultHandler;
    private String logPath = DEFAULT_CRASH_LOG_PATH;
    private long crashCacheSize = DEFAULT_CRASH_CACHE_SIZE;
    private boolean needUIReport = true;
    private boolean needToast = false;
    private int versionCode = 0;
    private final Object lock = new Object();

    private CrashHandler() {
    }

    /** @return the shared singleton. */
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    private void init(boolean toastUsable) {
        if (ContextUtils.isEmpty()) {
            return;
        }
        this.logPath = DEFAULT_CRASH_LOG_PATH + ContextUtils.getContext().getPackageName() + File.separator;
        this.crashCacheSize = DEFAULT_CRASH_CACHE_SIZE;
        this.needToast = toastUsable;
    }

    /** Installs this handler as the default uncaught-exception handler. */
    public void registerCrashHandler() {
        init(ConfigAgent.getBehaviorConfig().crashToastUsable);
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        handleException(throwable);
        Thread.UncaughtExceptionHandler handler = this.defaultHandler;
        if (handler != null) {
            handler.uncaughtException(thread, throwable);
        }
    }

    /** Enables or disables the UI crash report. */
    public void setCrashNeedUIReport(boolean needUIReport) {
        this.needUIReport = needUIReport;
    }

    /** Overrides the crash log directory. */
    public void setCrashLogPath(String logPath) {
        this.logPath = logPath;
    }

    /** Overrides the crash cache size; values below 1 are ignored. */
    public void setCrashCacheSize(long crashCacheSize) {
        if (crashCacheSize <= 0) {
            return;
        }
        this.crashCacheSize = crashCacheSize;
    }

    /** Loads the most recent crash report from disk. */
    public String loadLastCrashReport() {
        FileInputStream inputStream = null;
        String lastCrashReportFile = getLastCrashReportFile();
        if (lastCrashReportFile == null) {
            return null;
        }
        Properties properties = new Properties();
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        try {
            try {
                inputStream = new FileInputStream(new File(lastCrashReportFile));
                properties.load(inputStream);
                printWriter.println(lastCrashReportFile + " -----");
                printProperties(properties, printWriter);
                properties.clear();
                printWriter.println();
                printWriter.println();
                inputStream.close();
                return stringWriter.toString();
            } catch (Exception e) {
                LogUtil.e(TAG, e);
                if (inputStream != null) {
                    inputStream.close();
                }
                return stringWriter.toString();
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return stringWriter.toString();
        }
    }

    /** @return the absolute path of the newest crash report file. */
    private String getLastCrashReportFile() {
        String resolvedLogPath = resolveLogPath();
        String lastCrashReportFile = null;
        if (resolvedLogPath == null) {
            return null;
        }
        File logFile = new File(resolvedLogPath);
        if (logFile.isFile()) {
            return logFile.getAbsolutePath();
        }
        File[] files = logFile.listFiles();
        if (files == null) {
            return null;
        }
        long lastModified = 0;
        for (File file : files) {
            if (!file.isDirectory() && file.lastModified() >= lastModified) {
                lastCrashReportFile = file.getAbsolutePath();
                lastModified = file.lastModified();
            }
        }
        return lastCrashReportFile;
    }

    private void recordReStartCount() {
        int reStartCount = readReStartCount();
        if (DEBUG) {
            LogUtil.d(TAG, "read re-start count: " + reStartCount);
        }
        saveReStartCount(reStartCount + 1);
    }

    /** Resets the recorded restart count. */
    public void cleanReStartCount() {
        if (DEBUG) {
            LogUtil.d(TAG, "clean re-start count !!");
        }
        saveReStartCount(0);
    }

    private boolean isReStartTooMany() {
        return readReStartCount() >= RESTART_COUNT;
    }

    private boolean needUIReport() {
        return this.needUIReport;
    }

    private boolean handleException(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        handleExec(throwable);
        return true;
    }

    /** Collects and stores {@code throwable}. */
    public void handleExec(Throwable throwable) {
        if (ContextUtils.isEmpty()) {
            LogUtil.w(TAG, "进程死亡，无法保存异常");
            return;
        }
        try {
            collectCrash(throwable);
        } catch (Exception e) {
            LogUtil.e(TAG, "collectCrash error --> " + e);
            e.printStackTrace();
        }
    }

    private void collectCrash(Throwable throwable) {
        String stack;
        String stackMd5;
        boolean collect;
        synchronized (this.lock) {
            stack = getStack(throwable);
            try {
                stackMd5 = new MD5Coder().encode(stack.getBytes(Key.STRING_CHARSET_NAME));
            } catch (Exception e) {
                if (Constants.isDebug) {
                    LogUtil.e(TAG, e);
                }
                stackMd5 = "";
            }
            collect = isCollect(throwable);
        }
        if (!collect || TextUtils.isEmpty(stackMd5) || TextUtils.isEmpty(stack)) {
            return;
        }
        String stackTrace = formatStackTrace(stack);
        if (Constants.isDebug) {
            LogUtil.e("CrashHandler_Print", stackTrace);
        }
        ExceptionEvent exceptionEvent = new ExceptionEvent();
        exceptionEvent.reason = throwable.toString();
        exceptionEvent.stack = stackTrace;
        long internalStoreTotalSize = StoreUtils.getInternalStoreTotalSize();
        long internalStoreAvailableSize = StoreUtils.getInternalStoreAvailableSize();
        exceptionEvent.diskTotal = StoreUtils.convertSizeUnit(internalStoreTotalSize);
        exceptionEvent.diskUsage = StoreUtils.availablepercent(internalStoreAvailableSize, internalStoreTotalSize);
        long externalStoreTotalSize = StoreUtils.getExternalStoreTotalSize();
        long externalStoreAvailableSize = StoreUtils.getExternalStoreAvailableSize();
        exceptionEvent.sdTotal = StoreUtils.convertSizeUnit(externalStoreTotalSize);
        exceptionEvent.sdUsage = StoreUtils.availablepercent(externalStoreAvailableSize, externalStoreTotalSize);
        long memoryTotalSize = StoreUtils.getMemoryTotalSize();
        long memoryAvailable = StoreUtils.getMemoryAvailable(ContextUtils.getContext());
        exceptionEvent.memTotal = StoreUtils.convertSizeUnit(StoreUtils.getMemoryTotalSize());
        exceptionEvent.memUsage = StoreUtils.availablepercent(memoryAvailable, memoryTotalSize);
        exceptionEvent.functionName = EType.NAME_APP_EXCEPTION;
        exceptionEvent.dataCollectLevel = "B";
        exceptionEvent.dataSecurityLevel = "C";
        exceptionEvent.makeData();
        LogUtil.i(TAG, "prepare to insert !");
        if (exceptionEvent.stack.contains("UserBehaviorProvider.insert") || exceptionEvent.stack.contains("UserBehaviorProvider.bulkInsert")) {
            LogUtil.w(TAG, "crash occurs in UserBehaviorProvider.insert or bulkInsert , jump this one !");
        } else {
            ShareHelper.getInstance().insert(exceptionEvent.getContentValues());
            LogUtil.d(TAG, "record crash info done");
        }
    }

    private static String getStack(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        for (Throwable cause = throwable.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        String stack = stringWriter.toString();
        printWriter.close();
        return stack;
    }

    /** Handles a verify-error crash and triggers odex optimization when needed. */
    public void handleOdexCrash(Throwable throwable) {
        String stack = getStack(throwable);
        if (!TextUtils.isEmpty(stack) && odexDealSwitch && odexConfig != null && isVerifyError(stack)) {
            String odexInfoJson = SharedPrefUtils.getInstance().getKeyStringValue(DeviceInfo.ODEX_INFO, "");
            LogUtil.i(TAG, "handleOdex info json:" + odexInfoJson);
            OdexInfo odexInfo = JSONUtil.fromJSON(odexInfoJson, OdexInfo.class);
            String currentDate = DateFormatUtil.format(DateFormatUtil.FORMAT_2);
            if (odexInfo == null || !currentDate.equals(odexInfo.getCurrentDate())) {
                odexInfo = new OdexInfo(currentDate, 1);
                LogUtil.i(TAG, "handleOdex 为空或跨天，信息重置");
            } else {
                odexInfo.setTriggerCount(odexInfo.getTriggerCount() + 1);
                LogUtil.i(TAG, "handleOdex 当天次数+1");
            }
            SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.ODEX_INFO, JSONUtil.toJSON(odexInfo));
            if (System.currentTimeMillis() - odexInfo.getLastDealOdexTime() >= odexConfig.getInterval() * HOUR && odexInfo.getTriggerCount() >= odexConfig.getTriggerCount()) {
                odexInfo.setLastDealOdexTime(System.currentTimeMillis());
                odexInfo.setTriggerCount(0);
                SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.ODEX_INFO, JSONUtil.toJSON(odexInfo));
                odexOpt(ContextUtils.getContext(), throwable.getClass().getName());
            }
        }
    }

    private boolean isVerifyError(String stack) {
        if (TextUtils.isEmpty(stack)) {
            return false;
        }
        ArrayList<String> errorMarkers = new ArrayList<>();
        errorMarkers.add("java.lang.VerifyError");
        errorMarkers.add("java.lang.NoSuch");
        errorMarkers.add("AbstractMethodError");
        errorMarkers.add("NoClassDefFoundError");
        errorMarkers.add("IllegalAccessError");
        errorMarkers.add("IncompatibleClassChangeError");
        for (String marker : errorMarkers) {
            if (stack.contains(marker)) {
                return true;
            }
        }
        return false;
    }

    private long readFirstTime() {
        return SharedPrefUtils.getInstance().getKeyLongValue(KEY_FIRST_CRASH_TIME, 0L);
    }

    private long readRestartTime() {
        return SharedPrefUtils.getInstance().getKeyLongValue(KEY_LAST_CRASH_TIME, 0L);
    }

    private void saveReStartCount(int count) {
        if (count == 1) {
            SharedPrefUtils.getInstance().saveKeyLongValue(KEY_FIRST_CRASH_TIME, System.currentTimeMillis());
        }
        SharedPrefUtils.getInstance().saveKeyLongValue(KEY_LAST_CRASH_TIME, System.currentTimeMillis());
        SharedPrefUtils.getInstance().saveKeyIntValue(KEY_CRASH_COUNT, count);
    }

    private int readReStartCount() {
        return SharedPrefUtils.getInstance().getKeyIntValue(KEY_CRASH_COUNT, 0);
    }

    private String saveCrashInfoToFile(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        for (Throwable cause = throwable.getCause(); cause != null; cause = cause.getCause()) {
            cause.printStackTrace(printWriter);
        }
        String stackTrace = formatStackTrace(stringWriter.toString());
        printWriter.close();
        String resolvedLogPath = resolveLogPath();
        if (resolvedLogPath == null) {
            LogUtil.e(TAG, "log path invalid, can't save crash log file !!");
            return stackTrace;
        }
        String resolvedLogFileName = resolveLogFileName();
        if (resolvedLogFileName == null) {
            LogUtil.e(TAG, "log file name invalid, can't save crash log file !!");
            return stackTrace;
        }
        String filePath = resolvedLogPath + File.separator + resolvedLogFileName;
        if (!StoreUtils.checkFileDirExisted(filePath)) {
            LogUtil.e(TAG, "save crash info: create crash file dir error !");
            return stackTrace;
        }
        checkCacheSize();
        try {
            FileUtils.writeFile(new File(filePath), stackTrace, true);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
        return stackTrace;
    }

    private String formatStackTrace(String stack) {
        return TextUtils.concat("#", "\n", "#", new Date().toString(), "\n", STACK_TRACE, STACK_TRACE_SEPARATOR, stack).toString();
    }

    private String resolveLogPath() {
        String resolved = this.logPath;
        if (resolved != null) {
            return resolved;
        }
        try {
            return ContextUtils.getContext().getCacheDir().toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String resolveLogFileName() {
        try {
            Calendar calendar = Calendar.getInstance();
            return String.format(Locale.CHINA, "crash--%d-%d-%d-%d.%d.%d%s", Integer.valueOf(calendar.get(1)), Integer.valueOf(calendar.get(2) + 1), Integer.valueOf(calendar.get(5)), Integer.valueOf(calendar.get(11)), Integer.valueOf(calendar.get(12)), Integer.valueOf(calendar.get(13)), CRASH_REPORTER_EXTENSION);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void checkCacheSize() {
        String resolvedLogPath = resolveLogPath();
        if (resolvedLogPath != null && StoreUtils.getFileSize(resolvedLogPath) >= this.crashCacheSize) {
            LogUtil.d(TAG, "the cache size is rearch max size, we will clear it, clear: " + StoreUtils.deleteFolder(resolvedLogPath));
        }
    }

    private void printProperties(Properties properties, PrintWriter printWriter) {
        Enumeration<?> propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String key = (String) propertyNames.nextElement();
            printWriter.print(key);
            printWriter.print('=');
            String value = (String) properties.get(key);
            if (value == null) {
                value = "unknown.";
            }
            printWriter.println(value);
        }
    }

    /** Builds a fresh {@link CrashInfo} for {@code throwable}. */
    public static CrashInfo createCrashInfo(Throwable throwable) {
        CrashInfo crashInfo = new CrashInfo();
        crashInfo.setExceptionClass(throwable.getClass().getName());
        crashInfo.setExceptionUpdateTime(System.currentTimeMillis());
        crashInfo.setExceptionCount(1);
        crashInfo.setTodayCrashCount(1);
        crashInfo.setLastCrashDate(DateFormatUtil.format(DateFormatUtil.FORMAT_2));
        return crashInfo;
    }

    /** @return true when the crash should be collected given the throttle policy. */
    public static boolean isCollect(Throwable throwable) {
        CrashInfo crashInfo = JSONUtil.fromJSON(SharedPrefUtils.getInstance().getKeyStringValue(DeviceInfo.CRASH_INFO, ""), CrashInfo.class);
        if (crashInfo == null) {
            SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.CRASH_INFO, JSONUtil.toJSON(createCrashInfo(throwable)));
            return true;
        }
        LogUtil.i(TAG, "todayCount : " + crashInfo.getTodayCrashCount());
        if (!DateFormatUtil.format(DateFormatUtil.FORMAT_2).equals(crashInfo.getLastCrashDate())) {
            SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.CRASH_INFO, JSONUtil.toJSON(createCrashInfo(throwable)));
            return true;
        }
        if (crashInfo.getTodayCrashCount() >= DAY_MAX) {
            return false;
        }
        if (!throwable.getClass().getName().equals(crashInfo.getExceptionClass())) {
            crashInfo.setExceptionClass(throwable.getClass().getName());
            crashInfo.setExceptionCount(1);
            crashInfo.setExceptionUpdateTime(System.currentTimeMillis());
            crashInfo.setTodayCrashCount(crashInfo.getTodayCrashCount() + 1);
            SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.CRASH_INFO, JSONUtil.toJSON(crashInfo));
            return true;
        }
        double minDuration = crashInfo.getExceptionCount() > 5 ? Math.min(MAX_CRASH_REPORT_DURATION, Math.pow((crashInfo.getExceptionCount() - 5) + 1, 2.0d) * REPORT_DURATION) : REPORT_DURATION;
        if (System.currentTimeMillis() - crashInfo.getExceptionUpdateTime() <= minDuration) {
            return false;
        }
        crashInfo.setExceptionCount(crashInfo.getExceptionCount() + 1);
        crashInfo.setTodayCrashCount(crashInfo.getTodayCrashCount() + 1);
        crashInfo.setExceptionUpdateTime(System.currentTimeMillis());
        LogUtil.i(TAG, "连续采集 todayCount : " + crashInfo.getTodayCrashCount() + ",exceptionCount:" + crashInfo.getExceptionCount() + ", duration : " + minDuration + ",  lastExceptionTime : " + DateFormatUtil.format(DateFormatUtil.FORMAT_1, crashInfo.getExceptionUpdateTime()));
        SharedPrefUtils.getInstance().saveKeyStringValue(DeviceInfo.CRASH_INFO, JSONUtil.toJSON(crashInfo));
        return true;
    }

    /** Configures the odex optimization trigger. */
    public static void setOdexConfig(boolean odexSwitch, OdexConfig odexConfigParam) {
        odexDealSwitch = odexSwitch;
        if (odexConfigParam == null || !odexConfigParam.verify()) {
            LogUtil.i(TAG, "use default odexConfig");
            odexConfigParam = OdexConfig.getDefaultConfig();
        }
        LogUtil.i(TAG, "setOdexConfig:" + odexConfigParam);
        odexConfig = odexConfigParam;
    }

    /** Triggers a system-property based odex optimization and reports the event. */
    public void odexOpt(Context context, String exceptionName) {
        HashMap<String, String> extend;
        SystemPropertyUtil.setInt("persist.sys.opt.xtc_debug", 0);
        SystemPropertyUtil.set("persist.sys.debugopt.pkgname", context.getPackageName());
        LogUtil.i(TAG, "重新触发odex优化:" + context.getPackageName());
        if (TextUtils.isEmpty(exceptionName)) {
            extend = null;
        } else {
            extend = new HashMap<>();
            extend.put("exceptionName", exceptionName);
        }
        BehaviorUtil.customEvent(context, FUNCTION_ODEX_OPT, extend, "C", "C");
    }
}
