package com.xtc.utils.system;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xtc.bigdata.common.constants.Constants;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.NetworkInterface;

/** Device identity, memory, battery and vibration helpers. */
public class DeviceUtils {

    /** Fallback serial returned when no real identifier can be resolved. */
    public static final String DEFAULT_SERIAL = "1234567890";

    /** True when assertions are enabled for this class; keeps the original assert guards. */
    private static final boolean ASSERTIONS_ENABLED = DeviceUtils.class.desiredAssertionStatus();

    private static final String TAG = "DeviceUtils";
    private static final String WIFI_SERVICE = "wifi";
    private static final String VIBRATOR_SERVICE = "vibrator";
    private static final String ACTIVITY_SERVICE = "activity";

    /** Placeholder MAC address reported by WifiManager on newer systems. */
    private static final String PLACEHOLDER_MAC = "02:00:00:00:00:00";

    private DeviceUtils() {
        throw new UnsupportedOperationException("Are u ok ?");
    }

    /** {@code Build.VERSION.SDK_INT}. */
    public static int getSdkVersion() {
        return Build.VERSION.SDK_INT;
    }

    /** Telephony device id. */
    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getApplicationContext().getSystemService(Constants.PHONE)).getDeviceId();
    }

    /** Wi-Fi MAC address, falling back to the placeholder when unavailable. */
    public static String getMacAddress(Context context) {
        String macAddress;
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo connectionInfo;
        if (wifiManager == null || (connectionInfo = wifiManager.getConnectionInfo()) == null) {
            macAddress = "";
        } else {
            macAddress = connectionInfo.getMacAddress();
            if (PLACEHOLDER_MAC.equalsIgnoreCase(macAddress)) {
                macAddress = readHardwareAddress();
            }
        }
        return TextUtils.isEmpty(macAddress) ? PLACEHOLDER_MAC : macAddress;
    }

    /** Reads the wlan0 hardware address directly from the network interface. */
    private static String readHardwareAddress() {
        try {
            byte[] hardwareAddress = NetworkInterface.getByName("wlan0").getHardwareAddress();
            StringBuilder builder = new StringBuilder();
            for (byte value : hardwareAddress) {
                builder.append(String.format("%02X:", Byte.valueOf(value)));
            }
            if (builder.length() > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    /** {@code Build.MANUFACTURER}. */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /** {@code Build.MODEL} without whitespace. */
    public static String getModel() {
        String model = Build.MODEL;
        return model != null ? model.trim().replaceAll("\\s*", "") : model;
    }

    /** Vibrates the device for {@code duration} milliseconds. */
    public static void vibrate(Context context, long duration) {
        ((Vibrator) context.getApplicationContext().getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{0, duration}, -1);
    }

    /** @return true when the device reports a phone radio type. */
    public static boolean isPhone(Context context) {
        return ((TelephonyManager) context.getApplicationContext().getSystemService(Constants.PHONE)).getPhoneType() != 0;
    }

    /** Available memory in bytes. */
    public static long getAvailableMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /** Total memory in bytes. */
    public static long getTotalMemory(Context context) {
        if (Build.VERSION.SDK_INT >= 16) {
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ((ActivityManager) context.getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
            return memoryInfo.totalMem;
        }
        long totalMemory = 0;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile("/proc/meminfo", "r");
            totalMemory = Integer.parseInt(randomAccessFile.readLine().replaceAll("\\D+", ""));
            randomAccessFile.close();
            return totalMemory;
        } catch (Exception ignored) {
            return totalMemory;
        }
    }

    /** Number of processors available to the JVM. */
    public static int getProcessorCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    /** Current battery level, or -1 when unknown. */
    public static int getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (ASSERTIONS_ENABLED || batteryIntent != null) {
            return batteryIntent.getIntExtra("level", -1);
        }
        throw new AssertionError();
    }

    /** @return true when the battery is charging. */
    public static boolean isCharging(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        if (ASSERTIONS_ENABLED || batteryIntent != null) {
            return batteryIntent.getIntExtra("status", 1) == 2;
        }
        throw new AssertionError();
    }

    /** Best-effort device serial: bbk sn, then telephony id, then mac, then Build.SERIAL. */
    public static String getSerial(Context context) {
        String serial = readBbkSerial();
        if (TextUtils.isEmpty(serial)) {
            serial = getDeviceId(context);
        }
        if (TextUtils.isEmpty(serial)) {
            serial = getMacAddress(context);
        }
        if (TextUtils.equals(serial, PLACEHOLDER_MAC)) {
            serial = Build.SERIAL;
        }
        return TextUtils.isEmpty(serial) ? DEFAULT_SERIAL : serial;
    }

    /** Reads the last line of {@code /proc/bbksn}, or null. */
    public static String readBbkSerial() {
        BufferedReader reader = null;
        FileReader fileReader = null;
        String serial = null;
        try {
            fileReader = new FileReader("/proc/bbksn");
            reader = new BufferedReader(fileReader);
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    closeQuietly(fileReader);
                    closeQuietly(reader);
                    return serial;
                }
                serial = line;
            }
        } catch (Exception ignored) {
            closeQuietly(fileReader);
            closeQuietly(reader);
            return serial;
        }
    }

    /** Closes {@code closeable}, ignoring IO errors. */
    public static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception ignored) {
        }
    }

    /** {@code Build.VERSION.INCREMENTAL}. */
    public static String getIncrementalVersion() {
        return Build.VERSION.INCREMENTAL;
    }
}