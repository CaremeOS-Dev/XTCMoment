package com.xtc.httplib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xtc.bigdata.common.constants.Constants;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.netstate.NetStateDataManager;
import com.xtc.log.LogUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

/** Network type helpers used for the http request big-data tag. */
public class NetUtil {

    private static final String TAG = LogTag.tag("NetUtil");
    private static final String UNKNOWN_NETWORK = "未知网络类型";

    private NetUtil() {
    }

    /** Human readable network tag, e.g. {@code WIFI} or {@code 中国移动-4G}. */
    public static String getNetworkTag(Context context) {
        String tag;
        NetworkInfo activeNetworkInfo;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            String extraInfo = null;
            if (info != null && info.isAvailable()) {
                int type = info.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    tag = NetStateDataManager.WIFI;
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    tag = getSIMType(context) + ScreenshotUtils.SEPARATOR + getNetworkType(context);
                } else {
                    tag = extraInfo;
                }
                extraInfo = tag;
                if ("<unknown ssid>".equals(extraInfo)
                        && (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) != null
                        && activeNetworkInfo.isConnected()) {
                    extraInfo = activeNetworkInfo.getExtraInfo();
                }
            }
            return extraInfo == null ? UNKNOWN_NETWORK : extraInfo;
        } catch (Exception e) {
            LogUtil.w(TAG, e);
            return UNKNOWN_NETWORK;
        }
    }

    /** Coarse network generation, one of {@code WIFI/2G/3G/4G/未知网络类型}. */
    public static String getNetworkType(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return NetStateDataManager.WIFI;
            }
            if (type == ConnectivityManager.TYPE_MOBILE) {
                switch (((TelephonyManager) context.getSystemService(Constants.PHONE)).getNetworkType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_GSM:
                        return "2G";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return "3G";
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return "4G";
                    default:
                        return UNKNOWN_NETWORK;
                }
            }
        }
        return UNKNOWN_NETWORK;
    }

    /** SIM operator name, falling back to the MCC/MNC derived carrier. */
    public static String getSIMType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.PHONE);
        String simOperator = telephonyManager.getSimOperator();
        String simOperatorName = telephonyManager.getSimOperatorName();
        if (TextUtils.isEmpty(simOperatorName)) {
            if (simOperator == null) {
                simOperator = "未知运营商";
            } else if (simOperator.equals("46000") || simOperator.equals("46002") || simOperator.equals("46004")
                    || simOperator.equals("46007") || simOperator.equals("46008") || simOperator.equals("46013")) {
                simOperator = "中国移动";
            } else if (simOperator.equals("46001") || simOperator.equals("46006") || simOperator.equals("46009")) {
                simOperator = "中国联通";
            } else if (simOperator.equals("46003") || simOperator.equals("46005") || simOperator.equals("46011")
                    || simOperator.equals("46012") || simOperator.equals("46059")) {
                simOperator = "中国电信";
            } else if (simOperator.equals("46015")) {
                simOperator = "中国广电";
            } else if (TextUtils.isEmpty(simOperator)) {
                simOperator = "未知运营商";
            }
        } else {
            simOperator = simOperatorName;
        }
        LogUtil.i(TAG, "getSIMType:" + simOperator);
        return simOperator;
    }
}