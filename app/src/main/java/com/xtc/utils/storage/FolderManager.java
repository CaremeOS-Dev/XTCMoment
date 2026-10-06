package com.xtc.utils.storage;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.text.TextUtils;
import android.widget.Toast;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.utils.system.WatchModelUtil;

import java.io.File;

/** Resolves the on-device folders used by the moment module. */
public class FolderManager {

    private static final String TAG = "FolderManager";
    private static final String DEFAULT_ROOT = "/mnt/sdcard";
    private static final String XTC_DIR = "xtc";
    private static final String FACTORY_RESET_DIR = "factoryreset";
    private static final String FALLBACK_LAUNCHER_DIR = "/mnt/sdcard/xtc/ibwatch/launcher/";
    private static final String FALLBACK_COMMON_DIR = "/mnt/sdcard/xtc/ibwatch/common/";
    private static final int MIN_FREE_SPACE_GB = 10;
    private static final long BYTES_PER_GB = 1073741824L;
    private static volatile FolderManager instance;
    private static String externalRoot = DEFAULT_ROOT;

    private String appName;
    private String moduleName;
    private Context context;

    private FolderManager() {
    }

    /** Returns the singleton. */
    public static FolderManager getInstance() {
        FolderManager manager = instance;
        if (manager == null) {
            synchronized (FolderManager.class) {
                manager = instance;
                if (manager == null) {
                    manager = new FolderManager();
                    instance = manager;
                }
            }
        }
        return manager;
    }

    /** Configures the folder names. */
    public FolderManager configure(String appName, String moduleName) {
        this.appName = appName;
        this.moduleName = moduleName;
        return getInstance();
    }

    /** Configures the folder names and the context used for toasts. */
    public FolderManager configure(Context context, String appName, String moduleName) {
        this.context = context;
        this.appName = appName;
        this.moduleName = moduleName;
        return getInstance();
    }

    /** Module folder path (creating it when possible). */
    public String getModuleDir() {
        StringBuilder builder = new StringBuilder();
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            LogUtil.e("内存卡不可用");
            return FALLBACK_LAUNCHER_DIR;
        }
        if (TextUtils.isEmpty(externalRoot)) {
            refreshExternalRoot();
            externalRoot = Environment.getExternalStorageDirectory().getPath();
        }
        builder.append(externalRoot);
        builder.append(File.separator);
        builder.append(XTC_DIR);
        builder.append(File.separator);
        builder.append(this.appName);
        builder.append(File.separator);
        builder.append(this.moduleName);
        builder.append(File.separator);
        return FileUtils.makeDirs(builder.toString()) ? builder.toString() : FALLBACK_LAUNCHER_DIR;
    }

    /** Common folder path shared by all modules. */
    public String getCommonDir() {
        StringBuilder builder = new StringBuilder();
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            LogUtil.e(TAG, "内存卡不可用");
            return FALLBACK_COMMON_DIR;
        }
        if (TextUtils.isEmpty(externalRoot)) {
            refreshExternalRoot();
            externalRoot = Environment.getExternalStorageDirectory().getPath();
        }
        builder.append(externalRoot);
        builder.append(File.separator);
        builder.append(XTC_DIR);
        builder.append(File.separator);
        builder.append(this.appName);
        builder.append(File.separator);
        builder.append(FileConstants.COMMON);
        builder.append(File.separator);
        return FileUtils.makeDirs(builder.toString()) ? builder.toString() : FALLBACK_COMMON_DIR;
    }

    /** Creates a child directory of the module folder. */
    public void makeModuleDir(String child) {
        FileUtils.makeDirs(getModuleDir() + child + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
    }

    /** Creates a nested child directory of the module folder. */
    public void makeModuleDir(String parent, String child) {
        FileUtils.makeDirs(getModuleDir() + parent + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + child
                + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
    }

    /** Creates a nested path of directories below the module folder. */
    public void makeModuleDir(String[] children) {
        if (children == null || children.length <= 0) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        for (String child : children) {
            if (child.trim().length() > 0) {
                builder.append(child);
                builder.append(Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
            }
        }
        FileUtils.makeDirs(getModuleDir() + builder.toString());
    }

    /** Creates {@code parent/child} below the module folder. */
    public void makeModuleDir(String parent, String child, boolean nested) {
        FileUtils.makeDirs(new File(parent, child));
    }

    /** Creates a child directory of the common folder. */
    public void makeCommonDir(String child) {
        FileUtils.makeDirs(getCommonDir() + child + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER);
    }

    /** Absolute path of a child of the module folder, or an empty string. */
    public String getModulePath(String child) {
        String path = getModuleDir() + child;
        return FileUtils.makeDirs(path) ? path : "";
    }

    /** Absolute path of {@code child} under the module folder, or an empty string. */
    public String getModulePath(String child, String parent) {
        String path = getModuleDir() + parent;
        return FileUtils.makeDirs(path) ? path : "";
    }

    /** Path of the last uploaded app config. */
    public String getLastAppConfigPath() {
        return getRecoveryDir() + "last_AppConfig";
    }

    /** Path of the {@code AppConfig} file. */
    public String getConfigFilePath() {
        String path;
        if (WatchModelUtil.isNotRegionChangable()) {
            path = getCommonDir() + FileConstants.APP_CONFIG;
        } else {
            path = getOldQualFactoryResetPath() + FileConstants.APP_CONFIG;
        }
        LogUtil.d(TAG, "getConfigFilePathForNormalApp = " + path);
        return path;
    }

    /** Factory-reset backup directory for Qualcomm devices. */
    private String getOldQualFactoryResetPath() {
        String suffix = File.separator + XTC_DIR + File.separator + FACTORY_RESET_DIR + File.separator;
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            LogUtil.e(TAG, "getOldQualFactoryResetBackPath sdcard not mounted");
            return externalRoot + suffix;
        }
        String path = Environment.getExternalStorageDirectory() + suffix;
        if (FileUtils.makeDirs(path)) {
            return path;
        }
        LogUtil.e(TAG, "getOldQualFactoryResetBackPath mkdirs failed");
        return externalRoot + suffix;
    }

    /** Absolute path of a child of the common folder, or an empty string. */
    public String getCommonPath(String child) {
        String path = getCommonDir() + child;
        return FileUtils.makeDirs(path) ? path : "";
    }

    /** Warns (via toast) when the SD card has less than 10 GB free. */
    public boolean checkFreeSpace() {
        if (getFreeSpaceGb() >= MIN_FREE_SPACE_GB) {
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(FolderManager.this.context, "SD卡存储空间不足", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
        return true;
    }

    /** Free space on the external storage, in gigabytes. */
    private long getFreeSpaceGb() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize())) / BYTES_PER_GB) / BYTES_PER_GB;
    }

    /** Recovery directory used for the last app config. */
    public String getRecoveryDir() {
        String path = File.separator + "cache" + File.separator + "recovery" + File.separator;
        if (FileUtils.makeDirs(path)) {
            LogUtil.d(TAG, "getConfigFilePath dir exists");
        } else {
            LogUtil.e(TAG, "getConfigFilePath mkdirs failed");
        }
        return path;
    }

    /** Log collection directory reported by the system. */
    public static String getLogDir() {
        if (WatchModelUtil.isGaotong()) {
            return Environment.getExternalStorageDirectory().getPath() + File.separator + "Logs_Collector" + File.separator;
        }
        return Environment.getDataDirectory() + File.separator + "slog" + File.separator;
    }

    private void refreshExternalRoot() {
        externalRoot = DEFAULT_ROOT;
    }
}