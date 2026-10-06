package com.xtc.moment.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.xtc.contactapi.contact.manager.ContactManager;
import com.xtc.log.LogUtil;
import com.xtc.moment.db.Constants;
import com.xtc.moment.net.MomentHttpServiceProxy;
import com.xtc.moment.serve.FriendInfoServeImpl;
import com.xtc.moment.serve.ServerCache;
import com.xtc.virtualselfapi.manager.VirtualSelfInitManager;

/**
 * 权限常量与首次授权后的初始化逻辑。
 */
public class PermissionStringUtils {

    private static final String TAG = "PermissionStringUtils";

    public static volatile boolean isInitPermission = false;

    public static final String[] PERMISSIONS = {"android.permission.READ_CONTACTS"};
    public static final String[] SEND_PERMISSIONS = {
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"};
    public static final String[] LOCATION_PERMISSIONS = {
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"};
    public static final String[] FILE_PERMISSIONS = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE"};

    public static synchronized void checkPermissionForReTryBaseUrl(Context context) {
        LogUtil.d(TAG, "checkPermission: " + isInitPermission);
        if (!isInitPermission) {
            Context applicationContext = context.getApplicationContext();
            handlerBackgroundBusiness(applicationContext);
            VirtualSelfInitManager.getInstance().initData(applicationContext, Constants.DATABASE_NAME, false);
            isInitPermission = true;
            LogUtil.d(TAG, "checkPermission: set");
        }
    }

    private static void handlerBackgroundBusiness(final Context context) {
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                FileManager.initFolder();
                ContactManager.getInstance(context).reloadContact();
                FriendInfoServeImpl.getInstance(context);
                MomentHttpServiceProxy httpServiceProxy =
                        (MomentHttpServiceProxy) ServerCache.getHttpService(context, MomentHttpServiceProxy.class);
                httpServiceProxy.setInitBaseUrl(false);
                httpServiceProxy.checkBaseUrl();
            }
        });
    }

    private static boolean lacksPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == -1;
    }

    public static boolean lacksPermissions(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (lacksPermission(context, permission)) {
                LogUtil.e("TAG", "-------没有开启权限");
                return true;
            }
        }
        LogUtil.e("TAG", "-------权限已开启");
        return false;
    }
}