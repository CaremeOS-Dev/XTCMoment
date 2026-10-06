package com.xtc.system.account;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.bean.HttpConfig;
import com.xtc.utils.security.XtcSecurity;

/** Facade over the init-service HTTP configuration. */
public class AppInfoImpl {

    private static final String COLON = ":";
    private static final String TAG = "AppInfoImpl";
    private static volatile AppInfoImpl instance;

    private String grey;
    private HttpConfig httpConfig;
    private OnInitListener listener;
    private long registId;
    private String rsaPublicKey;
    private String version;

    /** Callback invoked after the initial config load. */
    public interface OnInitListener {
        void onFinish(boolean success);
    }

    public AppInfoImpl(Context context, OnInitListener listener) {
        this.listener = listener;
        initRsaPublicKeyAndGrey(context);
        initVersion(context);
    }

    public static AppInfoImpl getDefaultInstance(Context context) {
        if (instance == null) {
            synchronized (AppInfoImpl.class) {
                if (instance == null) {
                    instance = new AppInfoImpl(context, null);
                }
            }
        }
        return instance;
    }

    private void initRegistId() {
        this.registId = SDCardUtil.getRegistId();
    }

    private void initVersion(Context context) {
        this.version = new WatchDevice(context).getWatchVersion();
        if (WatchDevice.UNKOWN.equals(this.version)) {
            this.version = null;
            return;
        }
        this.version = "W_" + this.version;
    }

    private void initRsaPublicKeyAndGrey(Context context) {
        this.httpConfig = InitServiceData.getAppInfo(context);
        OnInitListener onInitListener = this.listener;
        if (onInitListener != null) {
            onInitListener.onFinish(this.httpConfig != null);
        }
    }

    public String getGrey() {
        HttpConfig config = this.httpConfig;
        if (config != null) {
            return config.getGrey();
        }
        return null;
    }

    public String getRsaPublicKey() {
        HttpConfig config = this.httpConfig;
        if (config != null) {
            return config.getRsaPublicKey();
        }
        return null;
    }

    /** Decrypts the self RSA public key stored by the init service. */
    public String getDecryptSelfRsaPublicKey() {
        String selfRsaPublicKey = getSelfRsaPublicKey();
        if (TextUtils.isEmpty(selfRsaPublicKey)) {
            return null;
        }
        String decrypted = XtcSecurity.decryptSecret(selfRsaPublicKey);
        if (TextUtils.isEmpty(decrypted) || !decrypted.contains(COLON)) {
            LogUtil.d(TAG, "setSelfRsaPublicKeyAndId: secretKeyDecrypt error");
            return null;
        }
        String[] parts = decrypted.split(COLON);
        if (parts.length < 2) {
            return null;
        }
        return parts[1];
    }

    public String getEncSwitch() {
        HttpConfig config = this.httpConfig;
        if (config != null) {
            return config.getEncSwitch();
        }
        return null;
    }

    public String getSelfRsaPublicKey() {
        HttpConfig config = this.httpConfig;
        if (config != null) {
            return config.getSelfRsaPublicKey();
        }
        return null;
    }

    public String getHttpHeadParam() {
        HttpConfig config = this.httpConfig;
        if (config != null) {
            return config.getHttpHeadParam();
        }
        return null;
    }

    public int getTs() {
        HttpConfig config = this.httpConfig;
        if (config != null) {
            return config.getTs();
        }
        return 0;
    }

    public String getAe() {
        HttpConfig config = this.httpConfig;
        if (config != null) {
            return config.getAe();
        }
        return null;
    }

    public String getVersion() {
        return this.version;
    }

    public Long getRegistId() {
        if (this.registId == 0) {
            initRegistId();
        }
        return Long.valueOf(this.registId);
    }
}