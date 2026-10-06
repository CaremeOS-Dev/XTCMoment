package com.xtc.bigdata.common.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xtc.bigdata.common.constants.Constants;
import com.xtc.log.LogUtil;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.NetworkInterface;

/** Device information helpers. */
public class DeviceUtils {

    private static final String DEFAULT_MAC = "02:00:00:00:00:00";
    public static final String DEFAULT_MACHINE_ID = "1234567890";
    private static final String PROPERTY_PRODUCT_INNER_MODEL = "ro.product.innermodel";
    private static final String TAG = "DeviceUtils";

    private DeviceUtils() {
        throw new UnsupportedOperationException("Are u ok ?");
    }

    public static int getSDK() {
        return Build.VERSION.SDK_INT;
    }

    public static String getIMEI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext()
                    .getSystemService(Constants.PHONE);
            return telephonyManager != null ? telephonyManager.getDeviceId() : "";
        } catch (Exception e) {
            LogUtil.e(TAG, "getIMEI error " + e.toString());
            return "";
        }
    }

    public static String getProductInnerModel() {
        return getStringSystemProperties(PROPERTY_PRODUCT_INNER_MODEL);
    }

    private static String getStringSystemProperties(String key) {
        try {
            Class<?> properties = Class.forName("android.os.SystemProperties");
            return (String) properties.getMethod("get", String.class).invoke(properties, key);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return "";
        }
    }

    public static String getMac(Context context) {
        String macAddress;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wifiManager == null ? null : wifiManager.getConnectionInfo();
        if (connectionInfo == null) {
            macAddress = "";
        } else {
            macAddress = connectionInfo.getMacAddress();
            if (DEFAULT_MAC.equalsIgnoreCase(macAddress)) {
                macAddress = getMac();
            }
        }
        return TextUtils.isEmpty(macAddress) ? DEFAULT_MAC : macAddress;
    }

    private static String getMac() {
        try {
            byte[] hardwareAddress = NetworkInterface.getByName("wlan0").getHardwareAddress();
            StringBuilder builder = new StringBuilder();
            for (byte b : hardwareAddress) {
                builder.append(String.format("%02X:", Byte.valueOf(b)));
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        } catch (Exception e) {
            if (!Constants.isDebug) {
                return null;
            }
            LogUtil.w(TAG, e + ":使用jdk方法获取mac地址失败");
            return null;
        }
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        String model = Build.MODEL;
        return model != null ? model.trim().replaceAll("\\s*", "") : model;
    }

    public static boolean isPhone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getApplicationContext()
                .getSystemService(Constants.PHONE);
        return telephonyManager != null && telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
    }

    public static long getMemoryAvailableSize(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
        }
        return memoryInfo.availMem;
    }

    public static long getMemoryTotalSize(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                activityManager.getMemoryInfo(memoryInfo);
            }
            return memoryInfo.totalMem;
        }
        long total = 0;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/proc/meminfo", "r");
            total = Integer.parseInt(randomAccessFile.readLine().replaceAll("\\D+", ""));
            randomAccessFile.close();
            return total;
        } catch (IOException e) {
            if (!Constants.isDebug) {
                return total;
            }
            LogUtil.w(TAG, e + ":读取/proc/meminfo去获取RAM大小失败");
            return total;
        }
    }

    public static int getCpuCoreSize() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static int getBatteryLevel(Context context) {
        return context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)).getIntExtra("level", -1);
    }

    public static boolean isCharge(Context context) {
        return context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)).getIntExtra("status", 1) == 2;
    }

    public static String getMachineId(Context context) throws Exception {
        String id = getBbkSn();
        if (TextUtils.isEmpty(id)) {
            id = getIMEI(context);
        }
        if (TextUtils.isEmpty(id)) {
            id = getMac(context);
        }
        if (TextUtils.equals(id, DEFAULT_MAC)) {
            id = Build.SERIAL;
        }
        return TextUtils.isEmpty(id) ? DEFAULT_MACHINE_ID : id;
    }

    public static String getBbkSn() throws Exception {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/bbksn"));
            String value = null;
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    return value;
                }
                value = line;
            }
        } catch (Exception e) {
            if (Constants.isDebug) {
                LogUtil.w(TAG, e + ":获取bbksn失败");
            }
            return null;
        } finally {
            closeIO(reader);
        }
    }

    public static void closeIO(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (IOException e) {
            if (Constants.isDebug) {
                LogUtil.w(TAG, e + ":关闭io失败, 这不科学 - -");
            }
        }
    }

    public static String getSystemVersion() {
        return Build.VERSION.INCREMENTAL;
    }
}