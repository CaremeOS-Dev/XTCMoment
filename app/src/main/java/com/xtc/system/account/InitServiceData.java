package com.xtc.system.account;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.system.account.bean.AppInfoBase;
import com.xtc.system.account.bean.HttpConfig;
import com.xtc.system.account.bean.ImAccountInfo;
import com.xtc.system.account.bean.WatchInfo;
import com.xtc.utils.encode.JSONUtil;
import com.xtc.utils.storage.ShareDBHelper;
import com.xtc.utils.storage.ShareUserIdUtil;

/** Reads account and HTTP config data from the launcher's providers. */
public class InitServiceData {

    private static final String DB_NAME = "i3launcher.db";
    private static final String PACKAGE_NAME = "com.xtc.i3launcher";
    public static final String TAG = "InitServiceData";

    /** App-info callbacks. */
    public interface OnGetAppInfoListener {
        void onFail();

        void onSuccess(HttpConfig httpConfig);
    }

    /** IM account callbacks. */
    public interface OnGetImAccountInfoListener {
        void onFail();

        void onSuccess(ImAccountInfo imAccountInfo);
    }

    /** Watch id callbacks. */
    public interface OnGetWatchIdListener {
        void onFail();

        void onSuccess(String watchId);
    }

    /** Watch info callbacks. */
    public interface OnGetWatchInfoListener {
        void onFail();

        void onSuccess(WatchInfo watchInfo);
    }

    private InitServiceData() {
    }

    public static String getWatchId(Context context) {
        return WatchAccountBase.getAccountWatchId(context);
    }

    public static void getWatchId(Context context, OnGetWatchIdListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        String watchId = WatchAccountBase.getAccountWatchId(context);
        if (TextUtils.isEmpty(watchId)) {
            LogUtil.e(TAG, "watchId is null");
            listener.onFail();
        } else {
            listener.onSuccess(watchId);
        }
    }

    /** Reads the HTTP config row from the init-service provider. */
    public static HttpConfig getAppInfo(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(AppInfoBase.CONTENT_URI, null, null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                String grey = getCursorStrValue(cursor, AppInfoBase.KEY_GREY);
                String rsaPublicKey = getCursorStrValue(cursor, AppInfoBase.KEY_RSA);
                String encSwitch = getCursorStrValue(cursor, AppInfoBase.KEY_ENC_SWITCH);
                String selfRsaPublicKey = getCursorStrValue(cursor, AppInfoBase.KEY_SELF_RSA_KEY);
                String httpHeadParam = getCursorStrValue(cursor, AppInfoBase.KEY_HTTP_HEAD);
                int tokenState = getCursorIntValue(cursor, AppInfoBase.KEY_TOKEN_STATE);
                String ae = getCursorStrValue(cursor, AppInfoBase.KEY_AES_ENCRYPT);
                LogUtil.i(TAG, "grey = " + grey + ", rsaPublicKey = " + rsaPublicKey + ", encSwitch = " + encSwitch
                        + ", selfRsaPublicKey = " + selfRsaPublicKey + ", httpHeadParam = " + httpHeadParam
                        + ", ts = " + tokenState + ", ae null = " + TextUtils.isEmpty(ae));
                cursor.close();
                return new HttpConfig(grey, encSwitch, rsaPublicKey, selfRsaPublicKey, httpHeadParam, tokenState, ae);
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static String getCursorStrValue(Cursor cursor, String column) {
        if (cursor == null || TextUtils.isEmpty(column)) {
            return null;
        }
        try {
            int index = cursor.getColumnIndex(column);
            if (index < 0) {
                return null;
            }
            return cursor.getString(index);
        } catch (Exception e) {
            LogUtil.e(TAG, "getCursorStrValue error: ", e);
            return null;
        }
    }

    private static int getCursorIntValue(Cursor cursor, String column) {
        if (cursor == null || TextUtils.isEmpty(column)) {
            return 0;
        }
        try {
            int index = cursor.getColumnIndex(column);
            if (index < 0) {
                return 0;
            }
            return cursor.getInt(index);
        } catch (Exception e) {
            LogUtil.e(TAG, "getCursorIntValue error: ", e);
            return 0;
        }
    }

    public static void getAppInfo(Context context, OnGetAppInfoListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        HttpConfig config = getAppInfo(context);
        if (config != null) {
            listener.onSuccess(config);
        } else {
            listener.onFail();
        }
    }

    /** Reads the watch info row from the watch-account provider. */
    public static WatchInfo getWatchInfo(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(WatchAccountBase.CONTENT_URI,
                    new String[]{"name", WatchAccountBase.KEY_NUMBER, WatchAccountBase.KEY_SHORT_NUMBER, "gender",
                            "grade", "birthday", "model", "innerModel"},
                    null, null, null);
            if (cursor != null && cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String number = cursor.getString(cursor.getColumnIndex(WatchAccountBase.KEY_NUMBER));
                String shortNumber = cursor.getString(cursor.getColumnIndex(WatchAccountBase.KEY_SHORT_NUMBER));
                String gender = cursor.getString(cursor.getColumnIndex("gender"));
                String grade = cursor.getString(cursor.getColumnIndex("grade"));
                Long birthday = Long.valueOf(cursor.getLong(cursor.getColumnIndex("birthday")));
                String model = cursor.getString(cursor.getColumnIndex("model"));
                String innerModel = cursor.getString(cursor.getColumnIndex("innerModel"));
                LogUtil.i(TAG, "number = " + number + ", shortNumber = " + shortNumber + ", name = " + name
                        + ", gender = " + gender + ", grade = " + grade + ", birthday = " + birthday + ", model = "
                        + model + ", innerModel = " + innerModel);
                WatchInfo watchInfo = new WatchInfo(name, number, shortNumber, gender, grade, birthday);
                watchInfo.setModel(model);
                watchInfo.setInnerModel(innerModel);
                return watchInfo;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    public static void getWatchInfo(Context context, OnGetWatchInfoListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        WatchInfo watchInfo = getWatchInfo(context);
        if (watchInfo != null) {
            listener.onSuccess(watchInfo);
        } else {
            listener.onFail();
        }
    }

    /** Reads the IM account info from the launcher's database. */
    public static void getImAccountInfo(Context context, final OnGetImAccountInfoListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }
        Context sharedContext = ShareUserIdUtil.createSharedContext(context, PACKAGE_NAME);
        if (sharedContext == null) {
            listener.onFail();
            return;
        }
        ShareDBHelper helper = null;
        try {
            helper = ShareUserIdUtil.openSharedDatabase(sharedContext, DB_NAME);
            helper.query("select * from watch_account", new ShareDBHelper.ICursorCallBack() {
                @Override
                public void callBack(Cursor cursor) {
                    if (cursor != null && cursor.moveToFirst()) {
                        listener.onSuccess((ImAccountInfo) JSONUtil.fromJSON(
                                cursor.getString(cursor.getColumnIndex("imAccountInfo")), ImAccountInfo.class));
                    } else {
                        listener.onFail();
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            });
        } catch (Exception e) {
            LogUtil.e(TAG, e);
            listener.onFail();
            if (helper != null) {
                helper.close();
            }
        }
    }
}