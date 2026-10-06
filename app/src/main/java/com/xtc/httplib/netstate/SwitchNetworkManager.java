package com.xtc.httplib.netstate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.text.TextUtils;

import com.xtc.httplib.HttpManager;
import com.xtc.httplib.LogTag;
import com.xtc.log.LogUtil;
import com.xtc.utils.storage.SharedManager;

import java.util.Calendar;

/** Switches the process onto the cellular network when the Wi-Fi stalls. */
public class SwitchNetworkManager {

    public static final String SEP_SP = "--";
    public static final String SP_SWITCH_DATA = LogTag.tag("SwitchNetworkManager") + "_switch_data";

    private static final String TAG = LogTag.tag("SwitchNetworkManager");
    private static volatile SwitchNetworkManager instance;

    private ConnectivityManager connectivityManager;
    private long nowDate;
    private int nowSwitchNum;
    private long preSwitchTime;
    private SharedManager sharedManager;
    private SwitchConfigBean configBean = new SwitchConfigBean();
    private volatile boolean isSwitchPhone = false;
    private boolean moduleSwitch = false;

    public static SwitchNetworkManager getInstance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new SwitchNetworkManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        listenNetworkCallback();
        this.sharedManager = SharedManager.getInstance(context);
        initSp();
    }

    public void setModuleSwitch(boolean moduleSwitch, SwitchConfigBean configBean) {
        LogUtil.i(TAG, "setModuleSwitch() moduleSwitch = [" + moduleSwitch + "], bean = [" + configBean + "]");
        this.moduleSwitch = moduleSwitch;
        this.configBean = configBean;
    }

    private void initSp() {
        String value = this.sharedManager.getString(SP_SWITCH_DATA, "");
        LogUtil.i(TAG, "initSP() string=" + value);
        if (value.isEmpty()) {
            setDefaultSp();
            return;
        }
        String[] parts = value.split(SEP_SP);
        if (parts.length != 2) {
            setDefaultSp();
            return;
        }
        try {
            this.nowDate = Long.parseLong(parts[0]);
            this.nowSwitchNum = Integer.parseInt(parts[1]);
        } catch (NumberFormatException ignored) {
            setDefaultSp();
        }
    }

    private void setDefaultSp() {
        this.nowDate = getCurrentZero();
        this.nowSwitchNum = 0;
    }

    private void listenNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(),
                    new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            super.onAvailable(network);
                            LogUtil.i(TAG, "onAvailable() network = [" + network + "]");
                            NetStateDataManager.getInstance().updateNetType();
                        }

                        @Override
                        public void onLost(Network network) {
                            super.onLost(network);
                            LogUtil.i(TAG, "onLost() network = [" + network + "]");
                            resetSwitch();
                        }
                    });
        }
    }

    /** @return true when the process was switched to the cellular network. */
    public boolean trySwitchNetwork() {
        if (!this.moduleSwitch) {
            return false;
        }
        if (!getWifiAssistant()) {
            LogUtil.i(TAG, "tryRetry() WifiAssistant off");
            return false;
        }
        if (System.currentTimeMillis() - this.preSwitchTime < this.configBean.getShakeTime()) {
            LogUtil.i(TAG, "tryRetry() wait " + this.configBean.getShakeTime() + " again");
            return false;
        }
        if (getCurrentZero() == this.nowDate) {
            if (this.nowSwitchNum >= this.configBean.getMaxDataNum()) {
                LogUtil.i(TAG, "tryRetry() Max number");
                return false;
            }
        } else {
            LogUtil.i(TAG, "tryRetry() next Date");
            setDefaultSp();
            saveSp();
        }
        return realSwitchNetwork();
    }

    private synchronized boolean realSwitchNetwork() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return false;
        }
        if (this.isSwitchPhone) {
            LogUtil.i(TAG, "switchToMobileData() switch resetSwitch");
            resetSwitch();
            return true;
        }
        Network[] networks = this.connectivityManager.getAllNetworks();
        if (networks.length < 2) {
            LogUtil.i(TAG, "tryRetry() getNetWorkNum() < 2");
            return false;
        }
        for (Network network : networks) {
            if (isCellular(network)) {
                LogUtil.i(TAG, "switchToMobileData() switch phone");
                this.isSwitchPhone = true;
                this.preSwitchTime = System.currentTimeMillis();
                saveSp();
                return this.connectivityManager.bindProcessToNetwork(network);
            }
        }
        LogUtil.i(TAG, "switchToMobileData() switch not");
        return false;
    }

    public int getNetSwitchNum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return this.connectivityManager.getAllNetworks().length;
        }
        return 0;
    }

    private void saveSp() {
        this.sharedManager.putString(SP_SWITCH_DATA, this.nowDate + SEP_SP + this.nowSwitchNum);
    }

    private boolean isCellular(Network network) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || network == null) {
            return false;
        }
        NetworkCapabilities capabilities = this.connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);
    }

    /** {@code persist.sys.wifi.assistant}, defaulting to true. */
    public static boolean getWifiAssistant() {
        try {
            Class<?> properties = Class.forName("android.os.SystemProperties");
            String value = (String) properties.getMethod("get", String.class).invoke(properties, "persist.sys.wifi.assistant");
            LogUtil.i(TAG, "propertyName:" + value);
            if (TextUtils.isEmpty(value)) {
                return true;
            }
            return Boolean.parseBoolean(value);
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            return true;
        }
    }

    public void autoReset() {
        if (this.isSwitchPhone && System.currentTimeMillis() - this.preSwitchTime > this.configBean.getResetTime()) {
            resetSwitch();
        }
    }

    public synchronized void countUserNum(long traffic) {
        if (this.isSwitchPhone) {
            this.nowSwitchNum = (int) (this.nowSwitchNum + traffic);
            saveSp();
        }
    }

    private synchronized void resetSwitch() {
        LogUtil.i(TAG, "resetSwitch() ");
        this.isSwitchPhone = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.connectivityManager.bindProcessToNetwork(null);
        }
    }

    public long getPreSwitchTime() {
        return this.preSwitchTime;
    }

    public long getCurrentZero() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
}