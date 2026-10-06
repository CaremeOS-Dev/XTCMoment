package com.xtc.web.client.manager;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.storage.SharedManager;
import com.xtc.utils.system.SystemProperty;
import com.xtc.utils.system.SystemPropertyUtil;
import com.xtc.utils.system.WatchModelUtil;
import com.xtc.web.client.data.AccountInfo;
import com.xtc.web.client.data.Constants;
import com.xtc.web.client.db.SpCache;
import com.xtc.web.core.callback.CompletionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 设备与账号基础信息查询，供 H5 通过 getAppData 批量获取。 */
public class BaseInfoManager {

    private static final String TAG = Constants.TAG + BaseInfoManager.class.getSimpleName();
    private static BaseInfoManager instance;

    /** H5 可查询的字段名。 */
    public interface Key {
        String APP_VERSION = "appVersion";
        String BIND_NUM = "bindNumber";
        String BIRTHDAY = "birthday";
        String COUNTRY_CODE = "countryCode";
        String DEVICE_ID = "deviceId";
        String GENDER = "gender";
        String GENIUS_NUMBER = "geniusNumber";
        String GRADE = "grade";
        String ICON_PATH = "icon";
        String LANGUAGE = "language";
        String MODEL = "model";
        String NAME = "name";
        String NET_TYPE = "network";
        String OPEN_ID = "openId";
        String PHONE_NUM = "phoneNumber";
        String REGION = "region";
        String SYSTEM_VERSION = "version";
        String WATCH_ID = "watchId";
    }

    private volatile AccountInfo accountInfo;
    private Context appContext;
    private String appVersion;
    private String bindNum;
    private String deviceId;
    private String innerModel;
    private String language;
    private String region;
    private SharedManager shareManager;
    private String systemVersion;
    private volatile boolean needRefreshData = true;
    private ExecutorService threadPool = Executors.newSingleThreadExecutor();

    public static BaseInfoManager getInstance(Context context) {
        if (instance == null) {
            instance = new BaseInfoManager(context);
        }
        return instance;
    }

    private BaseInfoManager(Context context) {
        this.appContext = context.getApplicationContext();
        this.shareManager = SharedManager.getInstance(this.appContext);
    }

    /** 按请求的字段列表组装信息，账号类字段会触发一次异步刷新。 */
    public void getAppData(List<String> keys, CompletionHandler<HashMap<String, String>> completionHandler) {
        HashMap<String, String> result = new HashMap<>();
        for (String key : keys) {
            if (Key.MODEL.equals(key)) {
                result.put(key, getInnerModel());
            } else if (Key.LANGUAGE.equals(key)) {
                result.put(key, getLanguage());
            } else if (Key.REGION.equals(key)) {
                result.put(key, getRegion());
            } else if (Key.SYSTEM_VERSION.equals(key)) {
                result.put(key, getSystemVersion());
            } else if (Key.BIND_NUM.equals(key)) {
                result.put(key, getBindNum());
            } else if (Key.NET_TYPE.equals(key)) {
                result.put(key, getNetworkType());
            } else if (Key.APP_VERSION.equals(key)) {
                result.put(key, getAppVersion());
            } else if (Key.WATCH_ID.equals(key)) {
                result.put(key, getWatchId());
            } else if (Key.PHONE_NUM.equals(key)) {
                result.put(key, getAccountInfo().getNumber());
            } else if (Key.COUNTRY_CODE.equals(key)) {
                result.put(key, getAccountInfo().getCountryCode());
            } else if (Key.OPEN_ID.equals(key)) {
                result.put(key, getAccountInfo().getOpenID());
            } else if (Key.NAME.equals(key)) {
                this.needRefreshData = true;
                result.put(key, getAccountInfo().getName());
            } else if (Key.ICON_PATH.equals(key)) {
                this.needRefreshData = true;
                result.put(key, getAccountInfo().getIconPath());
            } else if (Key.DEVICE_ID.equals(key)) {
                result.put(key, getDeviceId());
            } else if (Key.GRADE.equals(key)) {
                this.needRefreshData = true;
                result.put(key, String.valueOf(getAccountInfo().getGrade()));
            } else if (Key.GENDER.equals(key)) {
                this.needRefreshData = true;
                result.put(key, String.valueOf(getAccountInfo().getGender()));
            } else if (Key.BIRTHDAY.equals(key)) {
                this.needRefreshData = true;
                result.put(key, String.valueOf(getAccountInfo().getBirthday()));
            } else if (Key.GENIUS_NUMBER.equals(key)) {
                this.needRefreshData = true;
                result.put(key, String.valueOf(getAccountInfo().getGeniusNumber()));
            } else {
                result.put(key, "NotSupport");
            }
        }
        completionHandler.complete(result);
        LogUtil.d(TAG, "get app data complete!");
        if (this.needRefreshData) {
            this.threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    updateAccountInfo();
                    LogUtil.d(TAG, "update account info complete!");
                }
            });
        }
    }

    private String getDeviceId() {
        if (TextUtils.isEmpty(this.deviceId)) {
            this.deviceId = ((TelephonyManager) this.appContext.getSystemService(Context.TELEPHONY_SERVICE))
                    .getDeviceId();
        }
        return this.deviceId;
    }

    private String getInnerModel() {
        if (TextUtils.isEmpty(this.innerModel)) {
            this.innerModel = WatchModelUtil.getWatchInnerModel();
        }
        return this.innerModel;
    }

    private String getLanguage() {
        if (TextUtils.isEmpty(this.language)) {
            this.language = WatchModelUtil.getLanguage();
        }
        return this.language;
    }

    private String getRegion() {
        if (TextUtils.isEmpty(this.region)) {
            this.region = WatchModelUtil.getRegion();
        }
        return this.region;
    }

    private String getSystemVersion() {
        if (TextUtils.isEmpty(this.systemVersion)) {
            this.systemVersion = SystemPropertyUtil.get(SystemProperty.CURRENT_SOFT_VERSION, "");
        }
        return this.systemVersion;
    }

    private String getBindNum() {
        if (TextUtils.isEmpty(this.bindNum)) {
            this.bindNum = SystemPropertyUtil.get(SystemProperty.BOOT_BIND_NUMBER, "");
        }
        return this.bindNum;
    }

    private String getAppVersion() {
        if (TextUtils.isEmpty(this.appVersion)) {
            try {
                this.appVersion = String.valueOf(this.appContext.getPackageManager()
                        .getPackageInfo(this.appContext.getPackageName(), 0).versionCode);
            } catch (Exception e) {
                LogUtil.e(TAG, "get app version error = " + e);
            }
        }
        return this.appVersion;
    }

    private String getWatchId() {
        String watchId = SpCache.getWatchId(this.shareManager);
        return TextUtils.isEmpty(watchId) ? getAccountInfo().getWatchId() : watchId;
    }

    private AccountInfo getAccountInfo() {
        if (this.accountInfo == null) {
            updateAccountInfo();
        }
        return this.accountInfo;
    }

    /** 从账号 Provider 同步账号信息并缓存 watchId。 */
    private void updateAccountInfo() {
        Cursor cursor = null;
        try {
            AccountInfo info = new AccountInfo();
            cursor = WatchAccountBase.getWatchAccountCursor(this.appContext);
            if (cursor != null && cursor.moveToNext()) {
                String watchId = cursor.getString(cursor.getColumnIndex("watchId"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String openId = cursor.getString(cursor.getColumnIndex("openID"));
                String countryCode = "";
                String number;
                if (cursor.getColumnIndex(WatchAccountBase.KEY_NUMBER) == -1) {
                    LogUtil.d(TAG, "new version, query mobileNumberNew!");
                    number = cursor.getString(cursor.getColumnIndex("mobileNumberNew"));
                    countryCode = cursor.getString(cursor.getColumnIndex("countryCode"));
                } else {
                    number = cursor.getString(cursor.getColumnIndex(WatchAccountBase.KEY_NUMBER));
                }
                info.setWatchId(watchId);
                info.setName(name);
                info.setCountryCode(countryCode);
                info.setNumber(number);
                info.setOpenID(openId);
                info.setIconPath(WatchAccountBase.getLocalIconPath(this.appContext));
                info.setGeniusNumber(WatchAccountBase.getGeniusNumber(this.appContext));
                this.accountInfo = info;
                this.needRefreshData = false;
                SpCache.saveWatchId(this.shareManager, info.getWatchId());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /** 当前网络类型，用于 H5 展示。 */
    private String getNetworkType() {
        NetworkInfo networkInfo = ((ConnectivityManager) this.appContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return "None";
        }
        int type = networkInfo.getType();
        if (type == ConnectivityManager.TYPE_WIFI) {
            return "WiFi";
        }
        if (type != ConnectivityManager.TYPE_MOBILE) {
            return "None";
        }
        switch (((TelephonyManager) this.appContext.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType()) {
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
                return "None";
        }
    }
}