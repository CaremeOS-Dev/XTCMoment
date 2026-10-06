package com.xtc.bigdata.collector.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.httplib.netstate.NetStateDataManager;
import com.xtc.log.LogUtil;

/** Reports the current network type. */
public class NetworkUtil {

    private static final String TAG = "NetworkUtil";
    private static ConnectivityManager connectivityManager;
    private static NetworkUtil instance;

    private NetworkUtil() {
        if (ContextUtils.isEmpty()) {
            connectivityManager = null;
            return;
        }
        connectivityManager = (ConnectivityManager) ContextUtils.getContext().getSystemService("connectivity");
    }

    public static NetworkUtil getInstance() {
        if (instance == null) {
            instance = new NetworkUtil();
        }
        return instance;
    }

    /** Current network type name, or 无网络. */
    public String getNetworkType() {
        ConnectivityManager manager = connectivityManager;
        if (manager == null) {
            return "无网络";
        }
        NetworkInfo networkInfo = null;
        try {
            networkInfo = manager.getActiveNetworkInfo();
        } catch (Exception e) {
            LogUtil.e(TAG, "getNetworkType: ", e);
        }
        if (networkInfo == null || !networkInfo.isConnected()) {
            return "无网络";
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetStateDataManager.WIFI;
        }
        if (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
            return "无网络";
        }
        String subtypeName = networkInfo.getSubtypeName();
        switch (networkInfo.getSubtype()) {
            case 1:
            case 2:
            case 4:
            case 7:
            case 11:
                return "2G";
            case 3:
            case 5:
            case 6:
            case 8:
            case 9:
            case 10:
            case 12:
            case 14:
            case 15:
                return "3G";
            case 13:
                return "4G";
            default:
                return ("TD-SCDMA".equalsIgnoreCase(subtypeName) || "WCDMA".equalsIgnoreCase(subtypeName)
                        || "CDMA2000".equalsIgnoreCase(subtypeName)) ? "3G" : subtypeName;
        }
    }
}