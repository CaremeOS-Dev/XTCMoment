package com.xtc.system.account;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.xtc.log.LogUtil;
import com.xtc.system.account.bean.FunExtra;
import com.xtc.system.account.bean.FunSwitch;
import com.xtc.system.account.bean.NetFunItem;

import java.util.ArrayList;
import java.util.List;

/** Reads the watch account and feature-switch data from the providers. */
public class WatchAccountBase {

    public static final String AUTHORITY = "com.xtc.initservice.watchaccount";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.xtc.initservice";
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.xtc.initservice";
    public static final String DEFAULT_SORT_ORDER = "id asc";
    public static final int GDPR_FUNCTION_CLOSE = 0;
    public static final int GDPR_FUNCTION_OPEN = 1;
    public static final int ITEM = 1;
    public static final int ITEM_ID = 2;
    public static final String KEY_BIRTHDAY = "birthday";
    public static final String KEY_COUNTRY_CODE = "countryCode";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_GRADE = "grade";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_ID = "id";
    public static final String KEY_INNER_MODEL = "innerModel";
    public static final String KEY_MODEL = "model";
    public static final String KEY_NAME = "name";
    public static final String KEY_NUMBER = "number";
    public static final String KEY_REAL_NAME = "realName";
    public static final String KEY_SHORT_NUMBER = "shortNumber";
    public static final String KEY_WEIGHT = "weight";

    private static final String CONTENT_AUTHORITIES = "content://com.xtc.provider/low_authorities";
    private static final String CONTENT_COUNTRY_CODE = "content://com.xtc.provider/BaseDataProvider/countryCode/10";
    private static final String CONTENT_GENIUSACCOUNT_TYPE = "content://com.xtc.provider/BaseDataProvider/geniusaccount/7";
    private static final String CONTENT_ICONPATH_TYPE = "content://com.xtc.provider/BaseDataProvider/iconpath/6";
    private static final String CONTENT_ICON_TYPE = "content://com.xtc.provider/BaseDataProvider/icon/2";
    private static final String CONTENT_NAME_TYPE = "content://com.xtc.provider/BaseDataProvider/name/3";
    private static final String CONTENT_NUMBER_TYPE = "content://com.xtc.provider/BaseDataProvider/number/5";
    private static final String CONTENT_OPENID_TYPE = "content://com.xtc.provider/BaseDataProvider/openID/8";
    private static final String CONTENT_REAL_NAME = "content://com.xtc.provider/BaseDataProvider/realName/11";
    private static final String CONTENT_SIM_TYPE = "content://com.xtc.provider/BaseDataProvider/simType/9";
    private static final String CONTENT_TEMPERTATUREMAP_TYPE = "content://com.xtc.provider/BaseDataProvider/tempmap/4";
    private static final String CONTENT_WATCHID_TYPE = "content://com.xtc.provider/BaseDataProvider/watchId/1";
    private static final String TAG = WatchAccountBase.class.getSimpleName();

    public static final Uri CONTENT_URI = Uri.parse("content://com.xtc.initservice.watchaccount/item");

    private WatchAccountBase() {
    }

    public static Cursor getWatchAccountCursor(Context context) {
        return context.getContentResolver().query(Uri.parse(CONTENT_AUTHORITIES), null, null, null, null);
    }

    public static String getAccountWatchId(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_WATCHID_TYPE));
    }

    public static String getAccountIcon(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_ICON_TYPE));
    }

    public static String getAccountName(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_NAME_TYPE));
    }

    public static String getAccountTemperatureMap(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_TEMPERTATUREMAP_TYPE));
    }

    public static String getAccountNumber(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_NUMBER_TYPE));
    }

    public static String getAccountCountryCode(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_COUNTRY_CODE));
    }

    public static String getAccountSimType(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_SIM_TYPE));
    }

    public static String getLocalIconPath(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_ICONPATH_TYPE));
    }

    public static String getGeniusNumber(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_GENIUSACCOUNT_TYPE));
    }

    public static String getOpenID(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_OPENID_TYPE));
    }

    public static String getRealName(Context context) {
        return context.getContentResolver().getType(Uri.parse(CONTENT_REAL_NAME));
    }

    /** Module switch query; the provider returns 0 (on), 1 (hidden) or 2 (tip). */
    public static boolean queryModuleSwitchByBoolean(Context context, int module, boolean defaultValue) {
        int value = queryModuleSwitchByInt(context, module, defaultValue);
        if (value != 0) {
            return value != 1 && value == 2;
        }
        return true;
    }

    public static int queryModuleSwitchByInt(Context context, int module, boolean defaultValue) {
        Uri uri = Uri.parse("content://com.xtc.moduledata/query");
        String selection = "module=" + module;
        int fallback = !defaultValue ? 1 : 0;
        try {
            Cursor cursor = context.getContentResolver().query(uri, (String[]) null, selection, null, (String) null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        try {
                            int value = cursor.getInt(cursor.getColumnIndex("display"));
                            cursor.close();
                            logQueryModuleSwitchByInt(context, module, defaultValue, value);
                            return value;
                        } catch (Exception e) {
                            int value = defaultValue ? 0 : 1;
                            logQueryModuleSwitchByInt(context, module, defaultValue, value);
                            LogUtil.e(TAG, "queryModuleSwitchByInt getInt error", e);
                            cursor.close();
                            return value;
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
            logQueryModuleSwitchByInt(context, module, defaultValue, fallback);
            return fallback;
        } catch (Exception e) {
            logQueryModuleSwitchByInt(context, module, defaultValue, fallback);
            LogUtil.e(TAG, "queryModuleSwitchByInt query error", e);
            return fallback;
        }
    }

    private static void logQueryModuleSwitchByInt(Context context, int module, boolean defaultValue, int result) {
        LogUtil.d(TAG, "queryModuleSwitchByInt called: context = [" + context + "], module = [" + module
                + "], default = [" + defaultValue + "], result = " + result);
    }

    /** Tip text of the module switch. */
    public static String queryModuleSwitchTipByInt(Context context, int module, String defaultTip) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.xtc.moduledata/query"),
                (String[]) null, "module=" + module, null, (String) null);
        if (cursor != null && cursor.moveToFirst()) {
            String tip = cursor.getString(cursor.getColumnIndex("tips"));
            cursor.close();
            LogUtil.d(TAG, "queryModuleSwitchTipByInt called : context = [" + context + "], module = [" + module
                    + "], defaultTip = [" + defaultTip + "], tip = " + tip);
            return tip;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        LogUtil.d(TAG, "queryModuleSwitchTipByInt called : context = [" + context + "], module = [" + module
                + "], defaultTip = [" + defaultTip + "], cursor is null");
        return defaultTip;
    }

    /** Extra payload of the module switch. */
    public static String queryModuleSwitchExtraByInt(Context context, int module, String defaultExtra) {
        String extra = "";
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.xtc.moduledata/query"),
                (String[]) null, "module=" + module, null, (String) null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    try {
                        extra = cursor.getString(cursor.getColumnIndex("extra"));
                    } catch (Exception e) {
                        LogUtil.e(TAG, "" + e);
                    }
                    cursor.close();
                    LogUtil.d(TAG, "queryModuleSwitchExtraByInt called: context = [" + context + "], module = ["
                            + module + "], defaultExtra = [" + defaultExtra + "], extra = " + extra);
                    return extra;
                }
            } finally {
                cursor.close();
            }
        }
        LogUtil.d(TAG, "queryModuleSwitchExtraByInt called: context = [" + context + "], module = [" + module
                + "], defaultExtra = [" + defaultExtra + "], cursor is null.");
        return defaultExtra;
    }

    /** Reads the feature-switch record of the given package. */
    public static FunSwitch queryFunSwitchByPackageName(Context context, String packageName, boolean defaultValue) {
        int status;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.xtc.fundata/FunSwitch"),
                (String[]) null, "appPackage=\'" + packageName + "\'", null, (String) null);
        FunSwitch funSwitch = new FunSwitch();
        int switchId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            try {
                status = cursor.getInt(cursor.getColumnIndex("switchStatus"));
                switchId = cursor.getInt(cursor.getColumnIndex("switchId"));
            } catch (Exception e) {
                status = !defaultValue ? 1 : 0;
            } finally {
                cursor.close();
            }
            funSwitch.setAppPackage(packageName);
            funSwitch.setSwitchStatus(Integer.valueOf(status));
            funSwitch.setSwitchId(Integer.valueOf(switchId));
            LogUtil.d(TAG, "queryFunSwitchByPackageName called : context = [" + context + "], package = ["
                    + packageName + "], default = [" + defaultValue + "], id = " + switchId + ", status = " + status);
            return funSwitch;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        funSwitch.setSwitchId(-1);
        if (defaultValue) {
            funSwitch.setSwitchStatus(0);
        } else {
            funSwitch.setSwitchStatus(1);
        }
        LogUtil.d(TAG, "queryFunSwitchByPackageName called : context = [" + context + "], package = [" + packageName
                + "], default = [" + defaultValue + "], cursor is null!");
        return funSwitch;
    }

    /** Reads the feature-switch items of the given package. */
    public static List<NetFunItem> queryFunSwitchItemByPackageName(Context context, String packageName) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.xtc.fundata/FunItem"),
                (String[]) null, "appPackage=\'" + packageName + "\'", null, "id");
        ArrayList<NetFunItem> items = new ArrayList<>();
        if (cursor != null) {
            LogUtil.i(TAG, "funItem size = " + cursor.getCount());
            while (cursor.moveToNext()) {
                NetFunItem item = new NetFunItem();
                int itemId = cursor.getInt(cursor.getColumnIndex("itemId"));
                item.setAppPackage(packageName);
                item.setId(Integer.valueOf(itemId));
                items.add(item);
            }
            cursor.close();
            LogUtil.d(TAG, "queryFunSwitchItemByPackageName called : context = [" + context + "], package = ["
                    + packageName + "]");
            return items;
        }
        LogUtil.d(TAG, "queryFunSwitchItemByPackageName called : context = [" + context + "], package = ["
                + packageName + "], cursor is null!");
        return items;
    }

    /** Reads the feature-switch extras of the given package. */
    public static List<FunExtra> queryFunSwitchExtraByPackageName(Context context, String packageName) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.xtc.fundata/FunExtra"),
                (String[]) null, "appPackage=\'" + packageName + "\'", null, (String) null);
        ArrayList<FunExtra> extras = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String extra = cursor.getString(cursor.getColumnIndex("extra"));
                int itemId = cursor.getInt(cursor.getColumnIndex("itemId"));
                FunExtra funExtra = new FunExtra();
                funExtra.setItemId(Integer.valueOf(itemId));
                funExtra.setExtra(extra);
                extras.add(funExtra);
            }
            cursor.close();
        }
        LogUtil.d(TAG, "queryFunSwitchExtraByPackageName called : context = [" + context + "], package = ["
                + packageName + "]");
        return extras;
    }

    /** Reads the GDPR switch status. */
    public static int queryGdprSwitch(Context context, int switchId, int defaultValue) {
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.xtc.gdprdata/query"), null,
                "switchId=" + switchId, null, null);
        int status = defaultValue;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    status = cursor.getInt(cursor.getColumnIndex("status"));
                } catch (Exception e) {
                    LogUtil.e(TAG, "queryGdprSwitch error= ", e);
                    cursor.close();
                }
            }
            cursor.close();
        }
        LogUtil.d(TAG, "queryGdprSwitch called with: context = [" + context + "], module = [" + switchId
                + "], defalut = [" + defaultValue + "], result = " + status);
        return status;
    }

    public static boolean queryGdprSwitchByBoolean(Context context, int switchId, boolean defaultValue) {
        return 1 == queryGdprSwitch(context, switchId, defaultValue ? 1 : 0);
    }
}