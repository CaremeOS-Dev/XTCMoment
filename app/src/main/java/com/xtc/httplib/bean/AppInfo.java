package com.xtc.httplib.bean;

import android.text.TextUtils;

import com.xtc.bigdata.common.utils.ContextUtils;
import com.xtc.httplib.auth.HttpAuthManager;
import com.xtc.httplib.bigdata.RsaKeyBigData;
import com.xtc.httplib.cache.RequestCacheManager;
import com.xtc.httplib.okhttp.BaseInterceptor;
import com.xtc.log.LogUtil;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.security.XtcSecurity;

import java.util.Map;

/** Runtime view of the encryption and auth parameters. */
public class AppInfo {

    private static final String COLON = ":";
    private static final String ENCRYPT_OPEN = "1";
    private static final String TAG = "AppInfo";

    private volatile String aesKey;
    private String encSwitch;
    private volatile String encryptEebbkKey;
    private String grey;
    private String httpHeadParam;
    private volatile int httpTokenState;
    private volatile String keyId;
    private String rsaPublicKey;
    private volatile String selfRsaPublicKey;
    private String version;

    public String getGrey() {
        return this.grey;
    }

    public void setGrey(String grey) {
        this.grey = grey;
    }

    private String getRsaPublicKey() {
        return this.rsaPublicKey;
    }

    public void setRsaPublicKey(String rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
    }

    public String getEncSwitch() {
        return this.encSwitch;
    }

    public void setEncSwitch(String encSwitch) {
        this.encSwitch = encSwitch;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private boolean isEncrypt() {
        if (TextUtils.isEmpty(this.encSwitch)) {
            return true;
        }
        return ENCRYPT_OPEN.equals(this.encSwitch);
    }

    /** @return the RSA strategy to apply for the next request. */
    public int getRsaEncryptType() {
        if (!isEncrypt()) {
            return 0;
        }
        if (TextUtils.isEmpty(this.aesKey) || TextUtils.isEmpty(this.encryptEebbkKey) || TextUtils.isEmpty(this.keyId)) {
            return (TextUtils.isEmpty(this.selfRsaPublicKey) || TextUtils.isEmpty(this.keyId)) ? 1 : 2;
        }
        return 3;
    }

    /** Decrypts the {@code keyId:publicKey} pair pushed by the server. */
    public void setSelfRsaPublicKeyAndId(String encrypted) {
        if (TextUtils.isEmpty(encrypted)) {
            LogUtil.d(TAG, "setSelfRsaPublicKeyAndId: selfRsaPublicKeyAndId is empty");
            return;
        }
        String decrypted = XtcSecurity.decryptSecret(encrypted);
        if (TextUtils.isEmpty(decrypted) || !decrypted.contains(COLON)) {
            LogUtil.d(TAG, "setSelfRsaPublicKeyAndId: secretKeyDecrypt error");
            HttpAuthManager.getInstance().checkAuth(HttpAuthManager.AUTH_ERROR_CODE, 3);
            return;
        }
        String[] parts = decrypted.split(COLON);
        if (parts.length < 2) {
            LogUtil.d(TAG, "setSelfRsaPublicKeyAndId: decryptKeyAndId len error");
            return;
        }
        this.keyId = parts[0];
        this.selfRsaPublicKey = parts[1];
        LogUtil.d(TAG, "setSelfRsaPublicKeyAndId: 秘钥解析正常 ");
    }

    public String getKeyId() {
        return this.keyId;
    }

    public String getHttpHeadParam() {
        return this.httpHeadParam;
    }

    public void setHttpHeadParam(String httpHeadParam) {
        this.httpHeadParam = httpHeadParam;
    }

    /** Returns the RSA public key for the given strategy. */
    public String getPublicKey(int rsaEncryptType) {
        return rsaEncryptType == 2 ? this.selfRsaPublicKey : this.rsaPublicKey;
    }

    /** Parses the extra headers pushed by the server. */
    public Map<String, String> getHttpHeadParamMap() {
        if (!TextUtils.isEmpty(this.httpHeadParam)) {
            try {
                return (Map) JSONUtil.fromJSON(this.httpHeadParam, Map.class);
            } catch (Exception e) {
                LogUtil.w(TAG, "getHttpHeadParamMap", e);
            }
        }
        return null;
    }

    public String getEncryptEebbkKey() {
        if (TextUtils.isEmpty(this.encryptEebbkKey) || TextUtils.isEmpty(this.aesKey)) {
            return null;
        }
        return this.encryptEebbkKey;
    }

    /** Decrypts the {@code keyId:aesKey:eebbkKey} triple pushed by the server. */
    public void setEncryptEebbkKey(String encrypted) {
        if (TextUtils.isEmpty(encrypted)) {
            LogUtil.d(TAG, "setEncryptEebbkKey: encryptEebbkKey is empty");
            return;
        }
        String decrypted = XtcSecurity.decryptSecret(encrypted);
        if (TextUtils.isEmpty(decrypted) || !decrypted.contains(COLON)) {
            LogUtil.d(TAG, "setEncryptEebbkKey: Decrypt error");
            RsaKeyBigData.uploadKeyError(ContextUtils.getContext(), 3);
            return;
        }
        String[] parts = decrypted.split(COLON, 3);
        if (parts.length < 3 || TextUtils.isEmpty(parts[0]) || TextUtils.isEmpty(parts[1]) || TextUtils.isEmpty(parts[2])) {
            LogUtil.e(TAG, "setEncryptEebbkKey 解析异常");
            RsaKeyBigData.uploadKeyError(ContextUtils.getContext(), 3);
        } else {
            LogUtil.d(TAG, "setEncryptEebbkKey: 解析正常 ");
            this.keyId = parts[0];
            this.aesKey = parts[1];
            this.encryptEebbkKey = parts[2];
        }
    }

    public int getHttpTokenState() {
        return this.httpTokenState;
    }

    /** Updates the token state and notifies the cache on recovery. */
    public void setHttpTokenState(int state) {
        boolean wasValid = !isHttpTokenValid();
        this.httpTokenState = state;
        if (wasValid && isHttpTokenValid()) {
            RequestCacheManager.getInstance().onHttpTokenValid();
        }
    }

    public boolean isHttpTokenValid() {
        return this.httpTokenState == 0 || this.httpTokenState == 1;
    }

    /** AES key used to encrypt the request bodies. */
    public String getAesKey() {
        if (TextUtils.isEmpty(this.encryptEebbkKey) || TextUtils.isEmpty(this.aesKey)) {
            return BaseInterceptor.ENCRYPT_KEY;
        }
        return this.aesKey;
    }
}