package com.xtc.bigdata.collector.encapsulation.entity;

import android.os.Build;
import android.text.TextUtils;

import com.xtc.bigdata.collector.config.DeviceInfo;
import com.xtc.bigdata.collector.encapsulation.BaseAttrManager;
import com.xtc.bigdata.collector.utils.AppUtils;
import com.xtc.bigdata.common.constants.Constants;
import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.bigdata.common.utils.DeviceUtils;
import com.xtc.bigdata.common.utils.FileUtils;
import com.xtc.bigdata.common.utils.SharedPrefUtils;
import com.xtc.bigdata.common.utils.SystemInfoUtils;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.system.WatchModelUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Objects;

/** Persistent base attributes shared by every event. */
public class BaseAttr {

    private static final String BASE_ATTR = "base_attr";
    private static final String TAG = "BaseAttr";
    public static boolean isConfigChange = false;

    private int rooted;
    private String mId = "";
    private String devName = "";
    private String innerModel = "";
    private String osVer = "";
    private String brand = "";
    private String appId = "";
    private String appVersion = "";
    private String packageName = "";
    private String moduleName = "";

    public String getmId() {
        return this.mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getDevName() {
        return this.devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getInnerModel() {
        return this.innerModel;
    }

    public void setInnerModel(String innerModel) {
        this.innerModel = innerModel;
    }

    public String getOsVer() {
        return this.osVer;
    }

    public void setOsVer(String osVer) {
        this.osVer = osVer;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getModuleName() {
        return this.moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public int getRooted() {
        return this.rooted;
    }

    public void setRooted(int rooted) {
        this.rooted = rooted;
    }

    /** Builds the base attributes from the running device. */
    public void genBaseAttr() {
        LogUtil.i(TAG, "genBaseAttr begin");
        this.packageName = ContextUtils.getContext().getPackageName();
        this.devName = WatchModelUtil.getWatchInnerModel();
        this.innerModel = DeviceUtils.getProductInnerModel();
        if (Objects.equals(Constants.HOST_APP_ID, this.packageName)) {
            LogUtil.i(TAG, "host process , init some base attrs !");
            this.brand = Build.BRAND;
            this.mId = Build.SERIAL;
            if (!TextUtils.isEmpty(this.mId)) {
                this.osVer = Build.VERSION.RELEASE;
            } else {
                this.mId = DeviceUtils.getIMEI(ContextUtils.getContext());
                if (!TextUtils.isEmpty(this.mId)) {
                    this.osVer = Build.VERSION.RELEASE;
                } else {
                    String mac = DeviceUtils.getMac(ContextUtils.getContext());
                    if (TextUtils.isEmpty(mac)) {
                        mac = "unknown_machineid";
                    }
                    this.mId = mac;
                    this.osVer = Build.VERSION.RELEASE;
                }
            }
            if (Constants.deviceType.equals(Constants.WATCH)) {
                String config = readConfigFromFile(DeviceInfo.WATCH_CONFIG_PATH);
                if (TextUtils.isEmpty(config)) {
                    this.mId = "";
                    this.osVer = "";
                } else {
                    String[] parts = config.split("\\|");
                    if (parts.length >= 2) {
                        this.mId = parts[0];
                        this.osVer = parts[1];
                    }
                }
                LogUtil.i(TAG, "bigdata genBaseAttr config = " + toMask(this.mId) + "|" + this.osVer);
            }
            this.rooted = SystemInfoUtils.isRoot() ? 1 : 0;
        } else {
            LogUtil.w(TAG, "is not host process , skip some attrs initialization !");
        }
        try {
            this.appId = AppUtils.getAppId(ContextUtils.getContext());
        } catch (Exception e) {
            LogUtil.e(TAG, "getAppId  error = " + e);
            e.printStackTrace();
        }
        this.appVersion = AppUtils.getVersionName(ContextUtils.getContext());
        this.moduleName = AppUtils.getModuleName(ContextUtils.getContext());
        refreshBaseAttr(this);
    }

    private String readConfigFromFile(String name) {
        if (ContextUtils.isEmpty()) {
            return "";
        }
        FileUtils.ensureSdCardPath();
        try {
            return readFileString(FileUtils.getBigDataDirPath() + name);
        } catch (Exception e) {
            LogUtil.e(TAG, "readFileString error = " + e);
            return "";
        }
    }

    private String readFileString(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return "";
        }
        FileInputStream fileInputStream = null;
        InputStreamReader reader = null;
        try {
            fileInputStream = new FileInputStream(file);
            reader = new InputStreamReader(fileInputStream, "UTF-8");
            char[] buffer = new char[fileInputStream.available()];
            reader.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            if (Constants.isDebug) {
                LogUtil.e(TAG, "read file error = " + e);
                e.printStackTrace();
            }
            return "";
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception e) {
                LogUtil.e(TAG, "close error = " + e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return "BaseAttr{mId=\'" + toMask(this.mId) + "\', devName=\'" + this.devName + "\', innerModel=\'"
                + this.innerModel + "\', osVer=\'" + this.osVer + "\', brand=\'" + this.brand + "\', appId=\'"
                + toMask(this.appId) + "\', appVersion=\'" + this.appVersion + "\', packageName=\'" + this.packageName
                + "\', moduleName=\'" + this.moduleName + "\', rooted=" + this.rooted + '}';
    }

    public static synchronized BaseAttr getBaseAttr() {
        String json = SharedPrefUtils.getInstance().getKeyStringValue(BASE_ATTR, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return (BaseAttr) JSONUtil.fromJSON(json, BaseAttr.class);
    }

    /** Saves the watch config and refreshes the cached base attributes. */
    public static synchronized void saveConfig(String name, String content) {
        if (ContextUtils.isEmpty()) {
            return;
        }
        saveToBigDataDir(name, content);
        BaseAttr baseAttr = getBaseAttr();
        if (baseAttr == null) {
            LogUtil.w(TAG, "原sp中的 baseAttr = null , return !");
            return;
        }
        String[] parts = content.split("\\|");
        if (parts.length >= 2) {
            baseAttr.setmId(parts[0]);
            baseAttr.setOsVer(parts[1]);
        }
        refreshBaseAttr(baseAttr);
    }

    private static void saveToBigDataDir(String name, String content) {
        FileUtils.ensureSdCardPath();
        String dirPath = FileUtils.getBigDataDirPath();
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dirPath + name);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream outputStream = new FileOutputStream(file, false);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write(content);
            writer.flush();
            outputStream.flush();
            outputStream.close();
            writer.close();
        } catch (Exception e) {
            if (Constants.isDebug) {
                LogUtil.e(TAG, "write error = " + e);
                e.printStackTrace();
            }
        }
    }

    private static void refreshBaseAttr(BaseAttr baseAttr) {
        SharedPrefUtils.getInstance().saveKeyStringValue(BASE_ATTR, JSONUtil.toJSON(baseAttr));
        BaseAttrManager.setBaseAttr(baseAttr);
        LogUtil.i(TAG, "base attribute info = " + baseAttr);
    }

    private static String toMask(String value) {
        return isEmpty(value) ? value : toMaskImpl(value, 4, Math.max(1, value.length() - 5));
    }

    private static String toMaskImpl(String value, int start, int end) {
        if (isEmpty(value) || start >= value.length() || start > end) {
            return value;
        }
        int from = Math.max(start, 0);
        int to = Math.min(end, value.length() - 1);
        StringBuilder builder = new StringBuilder();
        if (from > 0) {
            builder.append(value, 0, from);
        }
        if (from <= to) {
            while (from <= to) {
                builder.append("*");
                from++;
            }
        }
        if (to < value.length() - 1) {
            builder.append(value, to + 1, value.length());
        }
        return builder.toString();
    }

    private static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }
}