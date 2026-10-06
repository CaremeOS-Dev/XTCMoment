package com.xtc.bigdata.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.xtc.log.LogUtil;

/**
 * 网络状态工具。
 */
public final class NetUtils {

    public static final int NETWORK_2G = 2;
    public static final int NETWORK_3G = 3;
    public static final int NETWORK_4G = 4;
    public static final int NETWORK_BLUETOOTH = 6;
    public static final int NETWORK_NO = -1;
    private static final int NETWORK_TYPE_GSM = 16;
    private static final int NETWORK_TYPE_IWLAN = 18;
    private static final int NETWORK_TYPE_TD_SCDMA = 17;
    public static final int NETWORK_UNKNOWN = 5;
    public static final int NETWORK_WIFI = 1;
    private static final String TAG = "NetUtils";
    private static final String NETWORK_TYPE_WIFI = "wifi";

    private NetUtils() {
        throw new UnsupportedOperationException("Are u ok ?");
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        return state != null && NetworkInfo.State.CONNECTED == state;
    }

    public static boolean isMobileDataConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        return state != null && NetworkInfo.State.CONNECTED == state;
    }

    public static void enableWifi(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return;
        }
        wifiManager.setWifiEnabled(enabled);
    }

    public static boolean isWifiEnable(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    @Deprecated
    public static boolean ping(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        try {
            return Runtime.getRuntime().exec("/system/bin/ping -c1 " + ip).waitFor() == 0;
        } catch (Exception e) {
            if (com.xtc.bigdata.common.constants.Constants.isDebug) {
                LogUtil.w(TAG, e + ":ping 网络出错");
            }
            return false;
        }
    }

    @Deprecated
    public static boolean ping(String ip, int count, int timeout) {
        if (StringUtils.isEmpty(ip)) {
            return false;
        }
        try {
            return Runtime.getRuntime().exec("/system/bin/ping -c" + count + " -w" + timeout + " " + ip).waitFor() == 0;
        } catch (Exception e) {
            if (com.xtc.bigdata.common.constants.Constants.isDebug) {
                LogUtil.w(TAG, e + ":ping 网络出错");
            }
            return false;
        }
    }

    public static int getNetworkType(NetworkInfo networkInfo) {
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return NETWORK_NO;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_WIFI;
        }
        if (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
            return networkInfo.getType() == ConnectivityManager.TYPE_BLUETOOTH ? NETWORK_BLUETOOTH : NETWORK_UNKNOWN;
        }
        switch (networkInfo.getSubtype()) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
            case NETWORK_TYPE_GSM:
                return NETWORK_2G;
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
            case NETWORK_TYPE_TD_SCDMA:
                return NETWORK_3G;
            case 13:
            case NETWORK_TYPE_IWLAN:
                return NETWORK_4G;
            default:
                String subtypeName = networkInfo.getSubtypeName();
                if ("TD-SCDMA".equalsIgnoreCase(subtypeName) || "WCDMA".equalsIgnoreCase(subtypeName) || "CDMA2000".equalsIgnoreCase(subtypeName)) {
                    return NETWORK_3G;
                }
                return NETWORK_UNKNOWN;
        }
    }

    public static int getNetWorkType(Context context) {
        return getNetworkType(getActiveNetworkInfo(context));
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return null;
        }
        return connectivityManager.getActiveNetworkInfo();
    }
}