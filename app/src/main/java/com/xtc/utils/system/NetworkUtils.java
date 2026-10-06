package com.xtc.utils.system;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import com.xtc.bigdata.common.constants.Constants;
import com.xtc.httplib.netstate.NetStateDataManager;
import com.xtc.log.LogUtil;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/** Network state helpers backed by the system connectivity services. */
public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String PREFERRED_NETWORK_MODE = "preferred_network_mode";
    private static final int NETWORK_MODE_GSM_ONLY = 6;
    private static final int NETWORK_MODE_GSM_UMTS = 16;
    private static final int NETWORK_MODE_LTE_GSM_WCDMA = 16;
    private static final int NETWORK_MODE_CDMA = 17;
    private static final int NETWORK_MODE_CDMA_LTE = 18;
    private static final int NETWORK_MODE_LTE_CDMA_EVDO = 21;
    private static final int NETWORK_MODE_LTE_ONLY = 22;

    /** Coarse network generation. */
    public enum NetworkType {
        NETWORK_WIFI,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    /** Coarse network generation with its numeric identifier. */
    public enum NetworkTypeInt {
        NETWORK_WIFI(1),
        NETWORK_4G(4),
        NETWORK_3G(3),
        NETWORK_2G(2),
        NETWORK_UNKNOWN(0),
        NETWORK_NO(0);

        public int Type;

        NetworkTypeInt(int type) {
            this.Type = type;
        }

        @Override
        public String toString() {
            return "NetworkTypeInt{Type=" + this.Type + '}';
        }
    }

    private NetworkUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /** Opens the wireless settings screen. */
    public static void openWirelessSettings(Context context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD) {
            context.startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
        } else {
            context.startActivity(new Intent("android.settings.SETTINGS"));
        }
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            return connectivityManager.getActiveNetworkInfo();
        }
        return null;
    }

    public static boolean isConnected(Context context) {
        return isConnectedOrConnecting(context);
    }

    /** @return true when a network is connected and available. */
    public static boolean isConnectedOrConnecting(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }

    /** @return true when the ping to a public address succeeds. */
    public static boolean isAvailableByPing(Context context) {
        ShellUtils.CommandResult result = ShellUtils.exec("ping -c 1 -w 1 123.125.114.144", false);
        boolean available = result.result == 0;
        if (result.errorMsg != null) {
            LogUtil.d("isAvailableByPing errorMsg", result.errorMsg);
        }
        if (result.successMsg != null) {
            LogUtil.d("isAvailableByPing successMsg", result.successMsg);
        }
        return available;
    }

    /** @return true when mobile data is enabled. */
    public static boolean isMobileDataEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.PHONE);
                Method method = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");
                if (method != null) {
                    Boolean enabled = (Boolean) method.invoke(telephonyManager);
                    if (enabled == null) {
                        LogUtil.e(TAG, "getDataEnabled invoke == null");
                        return false;
                    }
                    return enabled;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
        return isMobileDataEnabledLegacy(context, null);
    }

    private static boolean isMobileDataEnabledLegacy(Context context, Object[] args) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class<?> clazz = connectivityManager.getClass();
            return (Boolean) clazz.getMethod("getMobileDataEnabled",
                    args != null ? new Class[]{args.getClass()} : null).invoke(connectivityManager, args);
        } catch (Exception unused) {
            return false;
        }
    }

    /** Enables or disables mobile data. */
    public static void setMobileDataEnabled(Context context, boolean enabled) {
        if (CtaUtils.isCmcc()) {
            LogUtil.d(TAG, "current version is cta,we should not open mobile data by self");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.PHONE);
                Method method = telephonyManager.getClass().getDeclaredMethod("setDataEnabled", Boolean.TYPE);
                if (method != null) {
                    method.invoke(telephonyManager, enabled);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.getClass().getMethod("setMobileDataEnabled", Boolean.TYPE)
                    .invoke(connectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** @return true when mobile data is enabled and the network is cellular. */
    public static boolean isMobileConnected(Context context) {
        NetworkType networkType = getNetworkType(context);
        return isMobileDataEnabled(context)
                && (networkType.ordinal() == NetworkType.NETWORK_4G.ordinal()
                || networkType.ordinal() == NetworkType.NETWORK_3G.ordinal()
                || networkType.ordinal() == NetworkType.NETWORK_2G.ordinal());
    }

    /** @return true when the active network is LTE. */
    public static boolean is4G(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        return networkInfo != null && networkInfo.isAvailable()
                && networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    /** @return true when the active network is 3G. */
    public static boolean is3G(Context context) {
        return getNetworkType(context).ordinal() == NetworkType.NETWORK_3G.ordinal();
    }

    /** @return true when the active network is 2G. */
    public static boolean is2G(Context context) {
        return getNetworkType(context).ordinal() == NetworkType.NETWORK_2G.ordinal();
    }

    /** @return true when wifi is enabled. */
    public static boolean isWifiEnabled(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).isWifiEnabled();
    }

    /** Enables or disables wifi. */
    public static void setWifiEnabled(Context context, boolean enabled) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (enabled) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
        } else if (wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    /** @return true when a wifi network is connected. */
    public static boolean isWifiConnected(Context context) {
        NetworkInfo[] networkInfos;
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null && (networkInfos = connectivityManager.getAllNetworkInfo()) != null) {
            for (NetworkInfo networkInfo : networkInfos) {
                if (NetStateDataManager.WIFI.equals(networkInfo.getTypeName())
                        && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        && networkInfo.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /** @return true when wifi is enabled and connected. */
    public static boolean isWifiAvailable(Context context) {
        return isWifiEnabled(context) && isWifiConnected(context);
    }

    /** @return the network operator name. */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Constants.PHONE);
        if (telephonyManager != null) {
            return telephonyManager.getNetworkOperatorName();
        }
        return null;
    }

    /** @return the numeric identifier of the current network type. */
    public static int getNetworkTypeInt(Context context) {
        NetworkType networkType = getNetworkType(context);
        switch (networkType) {
            case NETWORK_WIFI:
                return NetworkTypeInt.NETWORK_WIFI.Type;
            case NETWORK_4G:
                return NetworkTypeInt.NETWORK_4G.Type;
            case NETWORK_3G:
                return NetworkTypeInt.NETWORK_3G.Type;
            case NETWORK_2G:
                return NetworkTypeInt.NETWORK_2G.Type;
            case NETWORK_NO:
                return NetworkTypeInt.NETWORK_NO.Type;
            default:
                return NetworkTypeInt.NETWORK_UNKNOWN.Type;
        }
    }

    /** @return the coarse network generation of the active network. */
    public static NetworkType getNetworkType(Context context) {
        NetworkInfo networkInfo = getActiveNetworkInfo(context);
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return NetworkType.NETWORK_NO;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return NetworkType.NETWORK_WIFI;
        }
        if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            switch (networkInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_GSM:
                    return NetworkType.NETWORK_2G;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return NetworkType.NETWORK_3G;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return NetworkType.NETWORK_4G;
                default:
                    String subtypeName = networkInfo.getSubtypeName();
                    if ("TD-SCDMA".equalsIgnoreCase(subtypeName) || "WCDMA".equalsIgnoreCase(subtypeName)
                            || "CDMA2000".equalsIgnoreCase(subtypeName)) {
                        return NetworkType.NETWORK_3G;
                    }
                    return NetworkType.NETWORK_UNKNOWN;
            }
        }
        return NetworkType.NETWORK_UNKNOWN;
    }

    /** @return a human readable network tag, e.g. {@code WIFI} or {@code 4G}. */
    public static String getNetworkTag(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return "no network";
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return NetStateDataManager.WIFI;
        }
        if (type == ConnectivityManager.TYPE_MOBILE) {
            int networkType = ((TelephonyManager) context.getSystemService(Constants.PHONE)).getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
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
                    return "unkonw network,network type:" + networkType;
            }
        }
        return "unkonw network,type:" + type;
    }

    /** @return the local IPv4 or IPv6 address, or null when unavailable. */
    public static String getLocalIpAddress(boolean isIpv4) {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address.isLoopbackAddress()) {
                        continue;
                    }
                    String hostAddress = address.getHostAddress();
                    boolean isIpv4Address = hostAddress.indexOf(58) < 0;
                    if (isIpv4) {
                        if (isIpv4Address) {
                            return hostAddress;
                        }
                    } else if (!isIpv4Address) {
                        int index = hostAddress.indexOf(37);
                        return index < 0 ? hostAddress.toUpperCase() : hostAddress.substring(0, index).toUpperCase();
                    }
                }
            }
            return null;
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** @return the resolved address of {@code domain}, or null when it cannot be resolved. */
    public static String getDomainAddress(final String domain) {
        try {
            return Executors.newCachedThreadPool().submit(new Callable<String>() {
                @Override
                public String call() {
                    try {
                        return InetAddress.getByName(domain).getHostAddress();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** @return true when airplane mode is on. */
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    /** Enables or disables airplane mode. */
    public static void setAirplaneMode(Context context, boolean enabled) {
        Settings.Global.putInt(context.getContentResolver(), "airplane_mode_on", enabled ? 1 : 0);
        Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
        intent.putExtra(com.xtc.moment.module.Constants.ProviderConstants.MOMENT_ITEM_STATE_PATH, enabled);
        context.sendBroadcast(intent);
    }

    /** Enables or disables VoLTE. */
    public static void setVolteEnabled(Context context, boolean enabled) {
        if (isVolteEnabled(context) == enabled) {
            return;
        }
        LogUtil.d(TAG, "setVltEnabled：" + enabled);
        try {
            Class<?> clazz = Class.forName("com.android.ims.ImsManager");
            clazz.getMethod("setEnhanced4gLteModeSetting", Context.class, Boolean.TYPE)
                    .invoke(clazz, context, enabled);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        }
    }

    /** @return true when VoLTE is enabled. */
    public static boolean isVolteEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            boolean enabled = false;
            try {
                Class<?> clazz = Class.forName("com.android.ims.ImsManager");
                Method userMethod = clazz.getMethod("isEnhanced4gLteModeSettingEnabledByUser", Context.class);
                Method ttyMethod = clazz.getMethod("isNonTtyOrTtyOnVolteEnabled", Context.class);
                boolean enabledByUser = (Boolean) userMethod.invoke(clazz, context);
                boolean nonTty = (Boolean) ttyMethod.invoke(clazz, context);
                LogUtil.i(TAG, "result1：" + enabledByUser + "，result2：" + nonTty);
                if (enabledByUser && nonTty) {
                    enabled = true;
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e);
            }
            LogUtil.i(TAG, "enh4glteMode：" + enabled);
            return enabled;
        }
        try {
            Class<?> clazz = Class.forName("com.android.ims.ImsManager");
            boolean enabled = (Boolean) clazz.getMethod("isEnhanced4gLteModeSettingEnabledByUser", Context.class)
                    .invoke(clazz, context);
            LogUtil.d(TAG, "enh4glteMode：" + enabled);
            return enabled;
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return false;
        }
    }

    /** Switches the preferred network mode based on the SIM operator. */
    public static boolean setPreferredNetworkMode(Context context, Integer networkMode) {
        int mode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mode = (networkMode != null && networkMode != 0) ? NETWORK_MODE_LTE_CDMA_EVDO : NETWORK_MODE_LTE_ONLY;
            return setPreferredNetworkModeInternal(context, mode);
        }
        int operator = SimManager.getOperatorType(context);
        LogUtil.d(TAG, "net work mode = " + networkMode + " operator = " + operator);
        mode = (networkMode != null && networkMode != 0) ? NETWORK_MODE_GSM_UMTS : NETWORK_MODE_GSM_ONLY;
        return setPreferredNetworkModeInternal(context, mode);
    }

    private static synchronized boolean setPreferredNetworkModeInternal(Context context, int networkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int subscriptionId = getDefaultSubscriptionId(context);
            int currentMode = getPreferredNetworkModeForSubscription(context, subscriptionId);
            LogUtil.i("NetWorkUtil", "networkMode=" + networkMode + ";currentNetworkMode=" + currentMode);
            if (currentMode == networkMode) {
                return false;
            }
            try {
                TelephonyManager.class.getMethod("setPreferredNetworkType", Integer.TYPE, Integer.TYPE)
                        .invoke((TelephonyManager) context.getSystemService(Constants.PHONE),
                                subscriptionId, networkMode);
                return true;
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LogUtil.e("NetWorkUtil", e);
                return false;
            }
        }
        int currentMode = getPreferredNetworkMode(context);
        LogUtil.i(TAG, "set preferred network mode = " + networkMode + "; currentNetworkMode = " + currentMode);
        if (currentMode == networkMode) {
            return true;
        }
        try {
            TelephonyManager.class.getMethod("setInternalPreferredNetworkType", Integer.TYPE)
                    .invoke((TelephonyManager) context.getSystemService(Constants.PHONE), networkMode);
            LogUtil.d(TAG, "set preferred network type success!");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "set preferred network type exception = " + e);
            return false;
        }
    }

    private static int getDefaultSubscriptionId(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return -1;
        }
        int subscriptionId = SubscriptionManager.getDefaultSubscriptionId();
        LogUtil.d(TAG, "defaultSubscriptionId = " + subscriptionId);
        return subscriptionId;
    }

    private static int getPreferredNetworkModeForSubscription(Context context, int subscriptionId) {
        int defaultValue = SystemPropertyUtil.getInt(SystemProperty.DEFAULT_NETWORK, 0);
        return Settings.Global.getInt(context.getContentResolver(),
                PREFERRED_NETWORK_MODE + subscriptionId, defaultValue);
    }

    private static int getPreferredNetworkMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "modem_preferred_network_mode",
                NETWORK_MODE_GSM_ONLY);
    }

    /** @return the currently preferred network mode. */
    public static int getCurrentNetworkMode(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            int mode = getPreferredNetworkModeForSubscription(context, getDefaultSubscriptionId(context));
            LogUtil.i(TAG, "currentNetworkMode：" + mode);
            return mode;
        }
        int mode = getPreferredNetworkMode(context);
        LogUtil.i(TAG, "currentPreferredNetworkMode：" + mode);
        return mode;
    }

    /** @return true when the active network is mobile data. */
    public static boolean isMobileNetworkConnected(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected()
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /** @return the network tag of the active connection, e.g. the wifi SSID. */
    public static String getConnectionTag(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        String tag = null;
        if (networkInfo != null && networkInfo.isAvailable()) {
            int type = networkInfo.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                WifiInfo wifiInfo = ((WifiManager) context.getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
                if (wifiInfo != null) {
                    tag = wifiInfo.getSSID();
                }
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                tag = SimManager.getSimOperatorName(context) + ScreenshotUtils.SEPARATOR + getNetworkType(context);
            }
        }
        return tag == null ? "unknown network tag" : tag;
    }

    /** Enables or disables data roaming. */
    public static void setDataRoamingEnabled(Context context, boolean enabled) {
        LogUtil.d(TAG, "setNetworkRoaming：" + enabled);
        Settings.Global.putInt(context.getContentResolver(), "data_roaming", enabled ? 1 : 0);
    }

    /** @return true when data roaming is enabled. */
    public static boolean isDataRoamingEnabled(Context context) {
        int mode = Settings.Global.getInt(context.getContentResolver(), "data_roaming", 0);
        LogUtil.d(TAG, "settingsNetworkMode = " + mode);
        return mode == 1;
    }
}