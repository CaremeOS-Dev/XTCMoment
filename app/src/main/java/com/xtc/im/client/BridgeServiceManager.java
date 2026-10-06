package com.xtc.im.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.xtc.im.aidl.IBridgeService;
import com.xtc.log.LogUtil;

/** 绑定宿主进程的 BridgeService，并通过 AIDL 提供 IM 能力。 */
public class BridgeServiceManager {

    private static final String BRIDGE_SERVICE_ACTION = "com.xtc.im.watch.bridge.BridgeService";
    private static final String BRIDGE_SERVICE_CLASS_NAME = "com.xtc.im.watch.bridge.BridgeService";
    private static final String HOST_PACKAGE_NAME = "com.xtc.i3launcher";
    private static final String TAG = LogTag.tag("BridgeServiceManager");
    private static volatile BridgeServiceManager instance;

    private volatile IBridgeService bridgeService;
    private Context context;
    private volatile boolean isBindSuccess;
    private Intent bridgeServiceIntent = createBridgeServiceIntent();
    private BridgeServiceConnection bridgeServiceConnection = new BridgeServiceConnection();

    public static BridgeServiceManager getInstance(Context context) {
        if (instance == null) {
            synchronized (BridgeServiceManager.class) {
                if (instance == null) {
                    instance = new BridgeServiceManager(context);
                }
            }
        }
        return instance;
    }

    private BridgeServiceManager(Context context) {
        this.context = context.getApplicationContext();
    }

    private Intent createBridgeServiceIntent() {
        Intent intent = new Intent();
        intent.setPackage(HOST_PACKAGE_NAME);
        intent.setComponent(new ComponentName(HOST_PACKAGE_NAME, BRIDGE_SERVICE_CLASS_NAME));
        intent.setAction(BRIDGE_SERVICE_ACTION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /** 拉起并绑定 BridgeService。 */
    public void bindBridgeService() {
        LogUtil.i(TAG, "bindBridgeService");
        unbindBridgeService();
        try {
            ComponentName componentName;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LogUtil.i(TAG, "bindBridgeService startForegroundService for 8.0");
                componentName = this.context.startForegroundService(this.bridgeServiceIntent);
            } else {
                componentName = this.context.startService(this.bridgeServiceIntent);
            }
            if (componentName == null) {
                LogUtil.e(TAG, "bindBridgeService start BridgeService failed.");
            } else {
                LogUtil.i(TAG, "bindBridgeService start BridgeService success.");
            }
            this.isBindSuccess = this.context.bindService(this.bridgeServiceIntent,
                    this.bridgeServiceConnection, Context.BIND_AUTO_CREATE);
            if (this.isBindSuccess) {
                LogUtil.i(TAG, "bindBridgeService bind BridgeService success.");
            } else {
                LogUtil.w(TAG, "bindBridgeService bind BridgeService failed.");
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "bindBridgeService error: " + e);
        }
    }

    /** 解绑 BridgeService。 */
    public void unbindBridgeService() {
        LogUtil.i(TAG, "unbindBridgeService");
        BridgeServiceConnection connection = this.bridgeServiceConnection;
        if (connection != null) {
            try {
                this.context.unbindService(connection);
            } catch (Exception e) {
                LogUtil.w(TAG, "unbindBridgeService Exception:" + e.toString());
            }
        } else {
            LogUtil.w(TAG, "unbindBridgeService serviceConnection is null.");
        }
        this.bridgeService = null;
    }

    public IBridgeService getBridgeService() {
        return this.bridgeService;
    }

    private class BridgeServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LogUtil.d(TAG, "BridgeServiceConnection onServiceConnected service = " + binder);
            bridgeService = IBridgeService.Stub.asInterface(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "BridgeServiceConnection onServiceDisconnected");
            bridgeService = null;
        }
    }
}