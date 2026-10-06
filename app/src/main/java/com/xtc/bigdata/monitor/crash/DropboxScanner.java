package com.xtc.bigdata.monitor.crash;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;

import com.xtc.bigdata.collector.ShareHelper;
import com.xtc.bigdata.collector.encapsulation.entity.attr.ApplicationAttr;
import com.xtc.bigdata.collector.encapsulation.entity.event.AEvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.DropBoxANREvent;
import com.xtc.bigdata.collector.encapsulation.entity.event.DropBoxCrashEvent;
import com.xtc.bigdata.collector.encapsulation.interfaces.IAttr;
import com.xtc.bigdata.collector.utils.DateFormatUtil;
import com.xtc.bigdata.common.utils.AbsAsyncBroadcastReceiver;
import com.xtc.bigdata.common.utils.FileUtils;
import com.xtc.bigdata.common.utils.SharedPrefUtils;
import com.xtc.log.LogUtil;
import com.xtc.moment.helper.ReminderHelper;
import com.xtc.moment.module.Constants;
import com.xtc.system.account.bean.AppInfoBase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** 扫描系统 DropBox 中的崩溃/ANR 记录并上报到数据采集通道。 */
public class DropboxScanner {

    private static final String COLLECT_TIME_KEY = "_collect_time";
    private static final String DATA_APP_ANR = "data_app_anr";
    private static final String DATA_APP_CRASH = "data_app_crash";
    private static final String SYSTEM_APP_ANR = "system_app_anr";
    private static final String SYSTEM_APP_CRASH = "system_app_crash";
    private static final String TAG = "DropboxScanner";
    private static final String TEST_ACTION = "test.action";
    private static final String TEST_KEY = "system_app_strictmode";
    private static final String DROPBOX_ENTRY_ADDED_ACTION = "android.intent.action.DROPBOX_ENTRY_ADDED";
    private static final long MAX_SCAN_ACTIVE_TASK = 10L;
    private static final long DROPBOX_DIR_MAX_SIZE = 20971520L;

    private static DropboxReceiver dropboxReceiver = null;
    private static List<String> keyList = new ArrayList<>();

    private StringBuilder sb;
    private boolean scanning;
    private ThreadPoolExecutor singleExecutor;
    private String stackTrace;
    private boolean debug = false;
    private volatile Map<String, Long> collectTimeMap = new HashMap<>();

    /** DropBox 新增记录的广播接收器。 */
    public class DropboxReceiver extends AbsAsyncBroadcastReceiver {
        boolean registed;

        synchronized void regist(Context context) {
            if (this.registed) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DROPBOX_ENTRY_ADDED_ACTION);
            intentFilter.addAction(TEST_ACTION);
            context.registerReceiver(this, intentFilter);
            this.registed = true;
        }

        synchronized void unregist(Context context) {
            if (this.registed) {
                context.unregisterReceiver(this);
                this.registed = false;
            }
        }

        @Override
        public void onReceiveAsync(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (DROPBOX_ENTRY_ADDED_ACTION.equals(action)
                    || (debug && TEST_ACTION.equals(action))) {
                startScan(context, intent.getStringExtra(AppInfoBase.KEY_TAG),
                        intent.getLongExtra("time", 0L) - 1);
            }
        }
    }

    public DropboxScanner() {
        dropboxReceiver = new DropboxReceiver();
        loadCollectTime();
        this.singleExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
    }

    private void loadCollectTime() {
        this.collectTimeMap.put(DATA_APP_CRASH, Long.valueOf(SharedPrefUtils.getInstance()
                .getKeyLongValue(DATA_APP_CRASH + COLLECT_TIME_KEY, 0L)));
        this.collectTimeMap.put(DATA_APP_ANR, Long.valueOf(SharedPrefUtils.getInstance()
                .getKeyLongValue(DATA_APP_ANR + COLLECT_TIME_KEY, 0L)));
        this.collectTimeMap.put(SYSTEM_APP_CRASH, Long.valueOf(SharedPrefUtils.getInstance()
                .getKeyLongValue(SYSTEM_APP_CRASH + COLLECT_TIME_KEY, 0L)));
        this.collectTimeMap.put(SYSTEM_APP_ANR, Long.valueOf(SharedPrefUtils.getInstance()
                .getKeyLongValue(SYSTEM_APP_ANR + COLLECT_TIME_KEY, 0L)));
    }

    private long getCollectTime(String tag) {
        Long collectTime = this.collectTimeMap.get(tag);
        if (collectTime == null) {
            return 0L;
        }
        return collectTime.longValue();
    }

    private void saveCollectTime(String tag, long collectTime) {
        this.collectTimeMap.put(tag, Long.valueOf(collectTime));
        SharedPrefUtils.getInstance().saveKeyLongValue(tag + COLLECT_TIME_KEY, collectTime);
        LogUtil.i(TAG, "update collectTime = " + collectTime + " of dropBoxTag = " + tag);
    }

    /** 注册广播并全量扫描一次。 */
    public void start(final Context context) {
        dropboxReceiver.regist(context);
        if (this.singleExecutor.isShutdown()) {
            this.singleExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
        }
        long activeCount = this.singleExecutor.getActiveCount();
        if (activeCount >= MAX_SCAN_ACTIVE_TASK) {
            LogUtil.w(TAG, "dropBox scan active task count is too many = " + activeCount);
            return;
        }
        this.singleExecutor.execute(new Runnable() {
            @Override
            public void run() {
                scanAndCollect(context, DATA_APP_CRASH, getCollectTime(DATA_APP_CRASH));
                scanAndCollect(context, DATA_APP_ANR, getCollectTime(DATA_APP_ANR));
                scanAndCollect(context, SYSTEM_APP_CRASH, getCollectTime(SYSTEM_APP_CRASH));
                scanAndCollect(context, SYSTEM_APP_ANR, getCollectTime(SYSTEM_APP_ANR));
                clearExpireDropBoxFile();
            }
        });
    }

    public void stop(Context context) {
        dropboxReceiver.unregist(context);
        this.singleExecutor.shutdownNow();
    }

    public void testScan(Context context) {
        startScan(context, TEST_KEY, 0L);
    }

    private void startScan(final Context context, final String tag, final long startTime) {
        synchronized (this) {
            if (this.scanning) {
                LogUtil.w(TAG, "dropBox is scanning , return !");
            } else {
                this.singleExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        scanAndCollect(context, tag, startTime);
                    }
                });
            }
        }
    }

    /** 从 startTime 之后逐条读取 DropBox 记录。 */
    private void scanAndCollect(Context context, String tag, long startTime) {
        if (!this.debug && !DATA_APP_CRASH.equals(tag) && !DATA_APP_ANR.equals(tag)
                && !SYSTEM_APP_CRASH.equals(tag) && !SYSTEM_APP_ANR.equals(tag)) {
            LogUtil.w(TAG, "do not care about this tag = " + tag);
            return;
        }
        if (getCollectTime(tag) > startTime) {
            LogUtil.w(TAG, tag + " this time = " + startTime + " happened in dropBox has scanned before !");
            return;
        }
        long scanStartTime = SystemClock.elapsedRealtime();
        DropBoxManager dropBoxManager = (DropBoxManager) context.getSystemService(Context.DROPBOX_SERVICE);
        if (dropBoxManager == null) {
            LogUtil.e(TAG, "error , dropBoxManager = null !");
            return;
        }
        int scannedCount = 0;
        try {
            synchronized (this) {
                this.scanning = true;
            }
            LogUtil.w(TAG, "start scan dropBoxTag = " + tag + " , time = " + startTime);
            while (true) {
                DropBoxManager.Entry entry = dropBoxManager.getNextEntry(tag, startTime);
                if (entry == null) {
                    break;
                }
                LogUtil.i(TAG, "scanned a dropBox entry,tag = " + entry.getTag() + " , time = "
                        + entry.getTimeMillis());
                InputStream inputStream = entry.getInputStream();
                if (inputStream == null) {
                    entry.close();
                    break;
                }
                dealWithOneException(tag, entry, inputStream);
                long entryTime = entry.getTimeMillis();
                saveCollectTime(tag, entryTime);
                entry.close();
                scannedCount++;
                startTime = entryTime;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "loop scan error = " + e);
            e.printStackTrace();
        } finally {
            synchronized (this) {
                this.scanning = false;
            }
        }
        LogUtil.i(TAG, "scan finished , count = " + scannedCount + " ,  this cost time = "
                + (SystemClock.elapsedRealtime() - scanStartTime));
    }

    private void dealWithOneException(String tag, DropBoxManager.Entry entry, InputStream inputStream) throws Exception {
        if (DATA_APP_CRASH.equals(tag) || SYSTEM_APP_CRASH.equals(tag)) {
            collectCrash(entry, inputStream);
        } else if (DATA_APP_ANR.equals(tag) || SYSTEM_APP_ANR.equals(tag)) {
            collectANR(entry, inputStream);
        } else {
            LogUtil.w(TAG, "error ! do not care about this tag = " + tag);
        }
    }

    /** 解析崩溃记录文本，提取进程名、包名与版本并上报。 */
    private void collectCrash(DropBoxManager.Entry entry, InputStream inputStream) throws Exception {
        LogUtil.w(TAG, "collectCrash begin ---");
        this.sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        long readStartTime = SystemClock.elapsedRealtime();
        String processName = "";
        String packageName = "";
        String versionName = "";
        boolean exceptionFound = false;
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                reader.close();
                LogUtil.d(TAG, "本次 crash , processName=" + processName + ", packageName=" + packageName
                        + ", versionName=" + versionName + ", read stream cost = "
                        + (SystemClock.elapsedRealtime() - readStartTime));
                collectCrashException(this.sb.toString(), processName, packageName, versionName);
                resetTemp();
                return;
            }
            if (exceptionFound) {
                this.sb.append(line);
                this.sb.append("\n");
            } else if (line.contains("Exception:")) {
                this.sb.append(line);
                this.sb.append("\n");
                exceptionFound = true;
            } else if (line.contains("Process:")) {
                processName = getProcessNames(line);
            } else if (line.contains("Package:")) {
                String[] packageAndVersion = getPackageAndVersionNames(line);
                packageName = packageAndVersion[0];
                versionName = packageAndVersion[1];
            } else {
                LogUtil.d(TAG, "do nothing ~");
            }
        }
    }

    private String[] getPackageAndVersionNames(String line) {
        String[] result = {"", ""};
        String[] parts = line.split(":");
        if (parts.length < 2) {
            return result;
        }
        String value = parts[1].trim();
        result[0] = value.split(" ")[0];
        if (value.contains("(")) {
            result[1] = value.substring(value.indexOf("(") + 1, value.indexOf(")"));
        }
        return result;
    }

    private String getProcessNames(String line) {
        String[] parts = line.split(":");
        return parts.length < 2 ? "" : parts[1].trim();
    }

    private void collectCrashException(String stackTrace, String processName, String packageName,
            String versionName) {
        this.stackTrace = stackTrace;
        LogUtil.i(TAG, "collectCrashException , stackTraceInfo = " + this.stackTrace);
        if (TextUtils.isEmpty(this.stackTrace)) {
            return;
        }
        DropBoxCrashEvent crashEvent = new DropBoxCrashEvent();
        crashEvent.functionName = "DropBox_Crash";
        if (!Objects.equals(processName, packageName)) {
            LogUtil.w(TAG, "Warning , processName != packageInfo !");
        }
        crashEvent.processName = processName;
        crashEvent.reason = this.stackTrace;
        crashEvent.stack = this.stackTrace;
        finalPackInfoAndInsert(crashEvent, packageName, versionName);
    }

    static {
        keyList.add("Process");
        keyList.add("Flags");
        keyList.add("Package");
        keyList.add("Activity");
        keyList.add("Parent-Process");
        keyList.add("Parent-Activity");
        keyList.add("Foreground");
        keyList.add("Subject");
        keyList.add("Build");
        keyList.add("Debugger");
    }

    /** 解析 ANR 记录头部的固定字段。 */
    private void collectANR(DropBoxManager.Entry entry, InputStream inputStream) throws Exception {
        LogUtil.w(TAG, "collectANR begin ---");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        long readStartTime = SystemClock.elapsedRealtime();
        DropBoxANREvent anrEvent = new DropBoxANREvent();
        anrEvent.functionName = "DropBox_ANR";
        int lineIndex = 0;
        boolean debuggerConnected = false;
        String line;
        while ((line = reader.readLine()) != null && (lineIndex = lineIndex + 1) < keyList.size() + 2) {
            if (TextUtils.isEmpty(line)) {
                continue;
            }
            String[] parts = line.split(":");
            if (parts.length < 2) {
                LogUtil.e(TAG, "line split array's length error !");
                continue;
            }
            String key = parts[0];
            String value = line.substring(line.indexOf(":") + 1).trim();
            LogUtil.d(TAG, key + "----" + value);
            switch (keyList.indexOf(key)) {
                case 0:
                    anrEvent.processName = value;
                    break;
                case 1:
                    anrEvent.flags = value;
                    break;
                case 2:
                    anrEvent.packageInfo = value;
                    break;
                case 3:
                    anrEvent.activity = value;
                    break;
                case 4:
                    anrEvent.parentProcess = value;
                    break;
                case 5:
                    anrEvent.parentActivity = value;
                    break;
                case 6:
                    anrEvent.foreground = value;
                    break;
                case 7:
                    anrEvent.subject = value;
                    break;
                case 8:
                    anrEvent.build = value;
                    break;
                case 9:
                    debuggerConnected = true;
                    break;
                default:
                    LogUtil.i(TAG, "don't care !");
                    break;
            }
        }
        reader.close();
        LogUtil.d(TAG, "本次 anr " + anrEvent + ", read stream cost = "
                + (SystemClock.elapsedRealtime() - readStartTime));
        if (debuggerConnected) {
            LogUtil.w(TAG, "debug connected when this anr occur , so don't collect !");
            return;
        }
        String[] packageAndVersion = getPV(anrEvent.packageInfo);
        finalPackInfoAndInsert(anrEvent, packageAndVersion[0], packageAndVersion[1]);
    }

    private String[] getPV(String packageInfo) {
        String[] result = {"", ""};
        if (TextUtils.isEmpty(packageInfo)) {
            return result;
        }
        result[0] = packageInfo.split(" ")[0];
        if (packageInfo.contains("(")) {
            result[1] = packageInfo.substring(packageInfo.indexOf("(") + 1, packageInfo.indexOf(")"));
        }
        return result;
    }

    private void resetTemp() {
        this.stackTrace = null;
        StringBuilder builder = this.sb;
        if (builder != null) {
            builder.delete(0, builder.length());
            this.sb = null;
        }
    }

    /** 把包名/版本写入 ApplicationAttr 后落库。 */
    private void finalPackInfoAndInsert(AEvent event, String packageName, String versionName) {
        ArrayList<IAttr> attrs = new ArrayList<>();
        for (IAttr attr : event.makeData()) {
            if (attr != null) {
                attrs.add(attr.clone());
            } else {
                attrs.add(null);
            }
        }
        for (int index = 0; index < attrs.size(); index++) {
            IAttr attr = attrs.get(index);
            if (attr instanceof ApplicationAttr) {
                ((ApplicationAttr) attr).setPackageName(packageName).setAppVersion(versionName);
                LogUtil.d(TAG, "after modify , final ApplicationAttr = " + attr);
                break;
            }
        }
        ShareHelper.getInstance().insert(event.getTempContentValues(attrs));
    }

    /** 清理过大或过期的 DropBox 文件。 */
    private void clearExpireDropBoxFile() {
        File dropBoxDir = new File(Environment.getExternalStorageDirectory().getPath() + "/bigData/dropBox/");
        File[] files = dropBoxDir.exists() ? dropBoxDir.listFiles() : null;
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            long fileLength = getFileLength(file);
            LogUtil.d(TAG, "total fileLength = " + fileLength);
            if (fileLength > DROPBOX_DIR_MAX_SIZE) {
                FileUtils.deleteDir(file);
                LogUtil.i(TAG, "delete anr file when file/Dir is larger than 20MB = " + file);
            } else if (System.currentTimeMillis() - file.lastModified() >= ReminderHelper.ONE_WEEK) {
                FileUtils.deleteDir(file);
                LogUtil.i(TAG, "delete expire dropBox file = " + file);
            }
        }
    }

    private long getFileLength(File file) {
        if (file == null) {
            return 0L;
        }
        if (file.isFile()) {
            return file.length();
        }
        if (file.isDirectory()) {
            return getDirFileLength(file);
        }
        LogUtil.w(TAG, "unknown file type = " + file);
        return 0L;
    }

    private long getDirFileLength(File dir) {
        if (dir == null) {
            return 0L;
        }
        long totalLength = 0;
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return 0L;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            long fileLength = 0;
            if (file.isFile()) {
                fileLength = file.length();
            } else if (file.isDirectory()) {
                fileLength = getDirFileLength(file);
            } else {
                LogUtil.w(TAG, "unknown file type = " + file);
            }
            totalLength += fileLength;
        }
        return totalLength;
    }

    private String saveStackInfoAsFile(String stackTrace, long time) {
        clearExpireDropBoxFile();
        return checkStackTraceInfoAndWrite(stackTrace, time);
    }

    private String checkStackTraceInfoAndWrite(String stackTrace, long time) {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/bigData/dropBox/"
                + DateFormatUtil.format(DateFormatUtil.FORMAT_2, System.currentTimeMillis())
                + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER;
        File dir = new File(dirPath);
        File[] files = dir.exists() ? dir.listFiles() : null;
        if (files == null || files.length == 0) {
            return saveToFile(dirPath, stackTrace, time + ".txt");
        }
        long fileTime = time;
        for (File file : files) {
            if (file != null && stackTrace.equals(readFileContent(file))) {
                String name = file.getName();
                String timeText = name.substring(0, name.indexOf(".txt"));
                LogUtil.i(TAG, "the same stack dropBox file name = " + timeText);
                fileTime = Long.valueOf(timeText).longValue();
                break;
            }
        }
        return saveToFile(dirPath, stackTrace, fileTime + ".txt");
    }

    private String saveToFile(String dirPath, String content, String name) throws Throwable {
        FileUtils.saveFile(dirPath, content, name);
        return dirPath + name;
    }

    private String readFileContent(File file) throws Throwable {
        int length = (int) file.length();
        if (length <= 0) {
            return "";
        }
        byte[] buffer = new byte[length];
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            inputStream.read(buffer, 0, length);
        } catch (IOException e) {
            LogUtil.e(TAG, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LogUtil.e(TAG, e);
                }
            }
        }
        return new String(buffer);
    }
}