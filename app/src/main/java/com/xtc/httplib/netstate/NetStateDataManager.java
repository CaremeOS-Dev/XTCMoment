package com.xtc.httplib.netstate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

import com.xtc.bigdata.common.constants.Constants;
import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.httplib.util.HandlerUtil;
import com.xtc.log.LogUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Tracks the current network type and signal strength. */
public class NetStateDataManager {

    public static final String MOBILE = "MOBILE";
    public static final String NO_NETWORK = "NO_NETWORK";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String WIFI = "WIFI";

    private static final String TAG = LogTag.tag("NetStateDataManager");
    private static volatile NetStateDataManager instance;

    private Context context;
    private String netType = UNKNOWN;
    private int signalStrengthValue = 0;
    private final List<NetChangeInterface> listeners = new ArrayList<>();
    private boolean haveSim = false;

    public static NetStateDataManager getInstance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new NetStateDataManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        this.context.registerReceiver(new NetworkChangeReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        listenPhoneState();
        SwitchNetworkManager.getInstance().init(this.context);
        updateNetType();
    }

    public synchronized void addNetChangeListener(NetChangeInterface listener) {
        this.listeners.add(listener);
    }

    public synchronized void removeNetChangeListener(NetChangeInterface listener) {
        this.listeners.remove(listener);
    }

    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNetType();
            synchronized (NetStateDataManager.this) {
                Iterator<NetChangeInterface> iterator = NetStateDataManager.this.listeners.iterator();
                while (iterator.hasNext()) {
                    iterator.next().netChange(NetStateDataManager.this.isConnected());
                }
            }
        }
    }

    public void updateNetType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                this.netType = WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                this.netType = MOBILE;
            } else {
                this.netType = UNKNOWN;
            }
        } else {
            this.netType = NO_NETWORK;
        }
        LogUtil.i(TAG, "updateNetType() " + this.netType);
        TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Constants.PHONE);
        if (telephonyManager == null) {
            return;
        }
        this.haveSim = TelephonyManager.SIM_STATE_READY == telephonyManager.getSimState();
        LogUtil.i(TAG, "updateNetType() haveSim " + this.haveSim);
    }

    private void listenPhoneState() {
        HandlerUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                final TelephonyManager telephonyManager = (TelephonyManager) NetStateDataManager.this.context
                        .getSystemService(Constants.PHONE);
                if (telephonyManager == null) {
                    return;
                }
                telephonyManager.listen(new PhoneStateListener() {
                    @Override
                    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                        super.onSignalStrengthsChanged(signalStrength);
                        NetStateDataManager.this.signalStrengthValue = signalStrength.getLevel();
                        LogUtil.i(TAG, "onSignalStrengthsChanged() signalStrengthValue = ["
                                + NetStateDataManager.this.signalStrengthValue + "]");
                    }
                }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        });
    }

    public String getNetType() {
        return this.netType;
    }

    public boolean isHaveSim() {
        return this.haveSim;
    }

    public int getSignalStrengthValue() {
        if (this.netType.equals(WIFI)) {
            return getWifiSignal();
        }
        if (this.netType.equals(NO_NETWORK)) {
            return 0;
        }
        return this.signalStrengthValue;
    }

    private int getWifiSignal() {
        WifiManager wifiManager = (WifiManager) this.context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return 0;
        }
        return WifiManager.calculateSignalLevel(wifiManager.getConnectionInfo().getRssi(), 5);
    }

    public boolean isConnected() {
        Context ctx = this.context;
        if (ctx == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
    }
}