package com.xtc.moment.serve;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.xtc.architecture.mvp.BaseServe;
import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.serve.bean.WatchAccountInfo;
import com.xtc.moment.util.FileManager;
import com.xtc.moment.util.PermissionStringUtils;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.web.client.manager.BaseInfoManager;

/**
 * 账号信息服务：维护当前手表账号的昵称、头像与 watchId 缓存。
 */
public class AccountInfoServerImpl extends BaseServe implements IAccountInfoServe {

    private static final String TAG = "AccountInfoServerImpl";

    private volatile boolean isInit;
    private volatile WatchAccountInfo watchAccountInfo;

    private AccountInfoServerImpl(Context context) {
        super(context);
        this.watchAccountInfo = new WatchAccountInfo();
        this.isInit = false;
        ServerCache.putBusinessServer(this);
    }

    public static IAccountInfoServe getInstance(Context context) {
        return ServerCache.getBusinessServer(context, AccountInfoServerImpl.class);
    }

    @Override
    public void initAllInfoDirectly() {
        LogUtil.d(TAG, " directly refresh all info");
        this.isInit = false;
        initWatchAccountInfo();
        FileManager.setMyIconPath(this.mContext);
    }

    @Override
    public synchronized void initWatchAccountInfo() {
        if (this.isInit) {
            return;
        }
        this.isInit = true;
        Cursor cursor = null;
        try {
            cursor = WatchAccountBase.getWatchAccountCursor(this.mContext);
            if (cursor != null && cursor.moveToNext()) {
                int iconColumn = cursor.getColumnIndex(BaseInfoManager.Key.ICON_PATH);
                this.watchAccountInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
                this.watchAccountInfo.setIcon(cursor.getString(iconColumn));
                refreshMyIconPath(this.mContext);
            }
            LogUtil.i(TAG, "initWatchAccountInfo: watchId: " + this.watchAccountInfo.getWatchId(this.mContext) + ", name: " + this.watchAccountInfo.getName(this.mContext) + ", icon: " + this.watchAccountInfo.getIcon());
        } catch (Exception e) {
            LogUtil.e(TAG, "query watch account cursor error = " + e);
            this.isInit = false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public WatchAccountInfo getWatchAccountInfo() {
        if (!this.isInit) {
            initWatchAccountInfo();
        }
        return this.watchAccountInfo;
    }

    @Override
    public String getMyHeadIconPath() {
        if (PermissionStringUtils.lacksPermissions(this.mContext, PermissionStringUtils.FILE_PERMISSIONS)) {
            return ContactManager.getInstance(this.mContext).getDefaultPortraitPath(this.mContext);
        }
        String sharedPath = FileManager.getSharedPath(this.mContext);
        if (!TextUtils.isEmpty(sharedPath)) {
            return sharedPath;
        }
        LogUtil.w(TAG, "getMyHeadIconPath icon is empty");
        return "";
    }

    @Override
    public void refreshMyIconPath(Context context) {
        FileManager.setMyIconPath(context);
    }

    @Override
    public boolean isInit() {
        return this.isInit;
    }

    @Override
    public void resetToInit() {
        this.isInit = false;
    }
}