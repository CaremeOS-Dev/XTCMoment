package com.xtc.moment.share.model;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;

import com.xtc.database.ormlite.DatabaseHelper;
import com.xtc.log.Log;
import com.xtc.log.LogUtil;
import com.xtc.moment.R;
import com.xtc.moment.constants.ModuleSwitchConstant;
import com.xtc.moment.db.Constants;
import com.xtc.moment.db.MomentDbManager;
import com.xtc.moment.share.other.ShareUtils;
import com.xtc.moment.util.switchs.ModuleSwitchUtil;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.watch.ConfigConstant;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 对外暴露应用分享白名单的 ContentProvider，供分享 SDK 查询模块开关与授权信息。
 */
public class AppShareProvider extends ContentProvider {

    private static final String TAG = ShareUtils.LOG + AppShareProvider.class.getSimpleName();
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final HashMap<String, String> appShareInfo;

    /** Provider 相关常量。 */
    private interface Key {
        int APP = 1;
        int APPS = 2;
        String APP_SHARE_ALLOW = "xtc_share_allow";
        String APP_SHARE_PACKAGE = "xtc_share_package";
        String APP_SHARE_TOKEN = "xtc_share_token";
        String AUTHORITY = "com.xtc.share.moment";
        String CREATE_VIEW = "create view app_share_view(xtc_share_package,xtc_share_allow,xtc_share_token) "
                + "as select packageName,allow,token from app_share; ";
        String DROP_VIEW = "drop view if exists app_share_view;";
        String MODULE_SWITCH = "moduleSwitch";
        String TABLE_NAME = "app_share_view";
        String TYPE_DIR = "vnd.android.cursor.dir/vnd.xtc.share";
        String TYPE_ITEM = "vnd.android.cursor.item/vnd.xtc.share";
    }

    static {
        uriMatcher.addURI(Key.AUTHORITY, ConfigConstant.Field.APP, Key.APPS);
        uriMatcher.addURI(Key.AUTHORITY, "app/#", Key.APP);
        uriMatcher.addURI(Key.AUTHORITY, "switch", Key.APP);

        appShareInfo = new HashMap<>();
        appShareInfo.put(Key.APP_SHARE_PACKAGE, Key.APP_SHARE_PACKAGE);
        appShareInfo.put(Key.APP_SHARE_ALLOW, Key.APP_SHARE_ALLOW);
        appShareInfo.put(Key.APP_SHARE_TOKEN, Key.APP_SHARE_TOKEN);
    }

    private String closeSwitchTip;
    private ContentResolver contentResolver;
    private Context context;
    private DatabaseHelper databaseHelper;

    @Override
    public boolean onCreate() {
        if (getContext() == null) {
            LogUtil.d(TAG, "context is null, create provider fail!");
            return false;
        }
        this.context = getContext();
        this.closeSwitchTip = this.context.getString(R.string.close_switch_tip);
        LogUtil.d(TAG, "app share provider create!");
        MomentDbManager.getInstance(this.context.getApplicationContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "app share provider query args = " + Arrays.toString(selectionArgs));
        this.databaseHelper = MomentDbManager.getInstance(this.context.getApplicationContext())
                .getDatabaseHelper();
        if (this.databaseHelper == null) {
            LogUtil.d(TAG, "app share provider query: databaseHelper == null");
            this.databaseHelper = DatabaseHelper.getInstance(getContext(), Constants.DATABASE_NAME);
        }
        if (this.contentResolver == null && getContext() != null) {
            this.contentResolver = getContext().getContentResolver();
        }
        SQLiteDatabase readableDatabase = this.databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        readableDatabase.execSQL(Key.DROP_VIEW);
        readableDatabase.execSQL(Key.CREATE_VIEW);
        queryBuilder.setTables(Key.TABLE_NAME);
        queryBuilder.setProjectionMap(appShareInfo);
        Cursor cursor = queryBuilder.query(readableDatabase, projection, selection, selectionArgs, null, null, null);
        cursor.setNotificationUri(this.contentResolver, uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        if (match == Key.APP) {
            return Key.TYPE_ITEM;
        }
        if (match == Key.APPS) {
            return Key.TYPE_DIR;
        }
        throw new IllegalArgumentException("Error Uri: " + uri);
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Log.d(TAG, "app provider call method = " + method);
        if (!Key.MODULE_SWITCH.equals(method) || extras == null) {
            return null;
        }
        int scene = extras.getInt("scene");
        if (scene == 1) {
            boolean switchOpen = ModuleSwitchUtil.queryModuleSwitchByBoolean(this.context,
                    ModuleSwitchConstant.MOMENT_MODULE_SWITCH, true);
            LogUtil.d(TAG, "query share to chat module switch = " + switchOpen);
            Bundle result = new Bundle();
            result.putBoolean(OpenApiConstant.ModuleSwitch.XTC_MODULE_SWITCH_OPEN, switchOpen);
            result.putString(OpenApiConstant.ModuleSwitch.XTC_MODULE_SWITCH_TIP, this.closeSwitchTip);
            return result;
        }
        if (scene != 2) {
            return null;
        }
        boolean switchOpen = ModuleSwitchUtil.queryModuleSwitchByBoolean(this.context,
                ModuleSwitchConstant.MOMENT_MODULE_SWITCH, true);
        LogUtil.d(TAG, "query share to moment module switch = " + switchOpen);
        Bundle result = new Bundle();
        result.putBoolean(OpenApiConstant.ModuleSwitch.XTC_MODULE_SWITCH_OPEN, switchOpen);
        result.putString(OpenApiConstant.ModuleSwitch.XTC_MODULE_SWITCH_TIP, this.closeSwitchTip);
        return result;
    }
}
