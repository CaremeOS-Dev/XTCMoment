package com.xtc.shareapi.share.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.xtc.log.LogUtil;
import com.xtc.secretapi.XTCGetUtil;
import com.xtc.shareapi.share.bean.DbApkInfo;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IObserverChangeListener;
import com.xtc.utils.common.CollectionUtil;
import com.xtc.utils.encode.JSONUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 分享支持管理器，负责查询 apk 鉴权信息、校验应用签名并监听鉴权数据变化。
 */
public class ShareSupportManager {

    private static final String TAG = "ShareSupportManager";
    private static final String XTCSERVICE_PACKAGE_NAME = "com.xtc.xws";

    private static final LinkedHashMap<String, DbApkInfo> APK_INFO_CACHE = new LinkedHashMap<>();

    private static volatile ShareSupportManager instance;

    private final Context context;
    private final ContentResolver contentResolver;

    private boolean isRegisterObserver;

    private final IObserverChangeListener observerChangeListener = new IObserverChangeListener() {
        @Override
        public void onApkObserverChange(Uri uri) {
            handleApkObserverChange(uri);
        }
    };

    private final ContentObserver conversationObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            observerChangeListener.onApkObserverChange(uri);
        }
    };

    private ShareSupportManager(Context context) {
        this.context = context.getApplicationContext();
        this.contentResolver = this.context.getContentResolver();
        registerContentObserver();
    }

    public static ShareSupportManager getInstance(Context context) {
        if (instance == null) {
            synchronized (ShareSupportManager.class) {
                if (instance == null) {
                    instance = new ShareSupportManager(context);
                }
            }
        }
        return instance;
    }

    private void handleApkObserverChange(Uri uri) {
        String url = uri.toString();
        if (TextUtils.isEmpty(url)) {
            Log.e(TAG, "IObserverChangeListener.onApkObserverChange: url is empty.");
            return;
        }
        Log.i(TAG, "IObserverChangeListener.onApkObserverChange: " + url);
        if (url.contains(OpenApiConstant.ShareSupport.URI_UPDATE_APK_INFO)) {
            ShareHandlerUtil.runOnBackground(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.i(TAG, "init#onNext: init = " + init());
                    } catch (Exception e) {
                        Log.e(TAG, "init#onError", e);
                    }
                }
            });
        }
    }

    private void registerContentObserver() {
        if (isRegisterObserver) {
            Log.d(TAG, "registerContentObserver fail: isRegisterObserver = true");
            return;
        }
        try {
            contentResolver.registerContentObserver(Uri.parse(OpenApiConstant.ShareSupport.URI_UPDATE_APK_INFO), true,
                    conversationObserver);
            isRegisterObserver = true;
            Log.d(TAG, "registerContactObserver successful");
        } catch (RuntimeException e) {
            Log.e(TAG, "registerContactObserver failure", e);
        }
    }

    /** 初始化并刷新 apk 鉴权信息缓存。 */
    public boolean init() {
        List<DbApkInfo> apkInfoList = queryAllApkInfo();
        if (apkInfoList == null || apkInfoList.size() == 0) {
            Log.e(TAG, "init: dbApkInfoList is null.");
            return false;
        }
        return updateApkInfoCache(apkInfoList);
    }

    private boolean updateApkInfoCache(List<DbApkInfo> apkInfoList) {
        APK_INFO_CACHE.clear();
        for (DbApkInfo apkInfo : apkInfoList) {
            if (apkInfo == null) {
                continue;
            }
            String packageName = apkInfo.getPackageName();
            if (TextUtils.isEmpty(packageName)) {
                Log.w(TAG, "updateApkInfoCache packageName is null");
            } else {
                APK_INFO_CACHE.put(packageName, apkInfo);
            }
        }
        return true;
    }

    /**
     * 判断指定应用是否通过分享鉴权。
     *
     * @return {@link OpenApiConstant.ShareSupport} 中定义的结果码。
     */
    public int isSupportShareFunction(String packageName) {
        if (APK_INFO_CACHE.size() == 0) {
            Log.i(TAG, "uninitialized.");
            return OpenApiConstant.ShareSupport.RESULT_UN_INIT;
        }
        if (getCertificateVersion(context) < 1) {
            return isSupportShareFunctionOld(packageName);
        }
        List<String> certificateList = JSONUtil.fromJSON(getApkCertificate(packageName, true), List.class, String.class);
        if (CollectionUtil.isEmpty(certificateList)) {
            LogUtil.e(TAG, "Verification failed, signature does not exist.");
            return OpenApiConstant.ShareSupport.RESULT_NON_EXIST;
        }
        for (String certificate : certificateList) {
            if (TextUtils.equals(XTCGetUtil.sign(context, certificate, packageName), XTCGetUtil.getSecret())) {
                LogUtil.i(TAG, "Verification succeeded.");
                return OpenApiConstant.ShareSupport.RESULT_SUCCESS;
            }
        }
        LogUtil.e(TAG, "Verification failed, signature inconsistent.");
        return OpenApiConstant.ShareSupport.RESULT_NOT_VERIFIED;
    }

    private String getApkCertificate(String packageName, boolean fromList) {
        if (TextUtils.isEmpty(packageName)) {
            Log.i(TAG, "getApkCertificate: packageName is null.");
            return null;
        }
        if (!APK_INFO_CACHE.containsKey(packageName)) {
            Log.i(TAG, "getApkCertificate: apkInfoCache don't has packageName " + packageName);
            return null;
        }
        DbApkInfo apkInfo = APK_INFO_CACHE.get(packageName);
        if (apkInfo == null) {
            Log.i(TAG, "getApkCertificate: localApkInfo == null");
            return null;
        }
        if (fromList) {
            return apkInfo.getCertificateList();
        }
        return apkInfo.getCertificate();
    }

    private List<DbApkInfo> queryAllApkInfo() {
        ContentResolver contentResolver = this.contentResolver;
        if (contentResolver == null) {
            Log.w(TAG, "mContentResolver == null");
            return null;
        }
        Cursor cursor = contentResolver.query(Uri.parse(OpenApiConstant.ShareSupport.URI_QUERY_ALL_APK_INFO),
                null, null, null, null);
        if (cursor == null) {
            Log.w(TAG, "queryAllApkInfo, cursor is null");
            return null;
        }
        List<DbApkInfo> apkInfoList = new ArrayList<>();
        if (cursor.moveToNext()) {
            String json = cursor.getString(0);
            if (TextUtils.isEmpty(json)) {
                Log.w(TAG, "queryAllApkInfo, cursorString is empty");
            } else {
                apkInfoList = JSON.parseArray(json, DbApkInfo.class);
            }
        }
        cursor.close();
        return apkInfoList;
    }

    private int getCertificateVersion(Context context) {
        try {
            int version = context.getPackageManager()
                    .getApplicationInfo(XTCSERVICE_PACKAGE_NAME, PackageManager.GET_META_DATA)
                    .metaData.getInt(OpenApiConstant.App.META_DATA_XTCSERVICE_VERSION);
            LogUtil.i(TAG, "getCertificateVersion = " + version);
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e(TAG, "getCertificateVersion error = " + e);
            return -1;
        }
    }

    private int isSupportShareFunctionOld(String packageName) {
        LogUtil.i(TAG, "isSupportShareFunctionOld");
        String certificate = getApkCertificate(packageName, false);
        if (TextUtils.isEmpty(certificate)) {
            LogUtil.e(TAG, "Verification failed, old signature does not exist.");
            return OpenApiConstant.ShareSupport.RESULT_NON_EXIST;
        }
        if (!TextUtils.equals(XTCGetUtil.sign(context, certificate, packageName), XTCGetUtil.getSecret())) {
            LogUtil.e(TAG, "Verification failed, old signature inconsistent.");
            return OpenApiConstant.ShareSupport.RESULT_NOT_VERIFIED;
        }
        LogUtil.i(TAG, "old Verification succeeded.");
        return OpenApiConstant.ShareSupport.RESULT_SUCCESS;
    }
}