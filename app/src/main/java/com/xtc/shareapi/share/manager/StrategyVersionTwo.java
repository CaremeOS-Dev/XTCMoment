package com.xtc.shareapi.share.manager;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.xtc.moment.R;
import com.xtc.shareapi.share.bean.AppInfo;
import com.xtc.shareapi.share.bean.ModuleSwitch;
import com.xtc.shareapi.share.communication.SendMessageToXTC;
import com.xtc.shareapi.share.constant.OpenApiConstant;
import com.xtc.shareapi.share.interfaces.IShareObject;
import com.xtc.shareapi.share.interfaces.Scene;
import com.xtc.shareapi.share.shareobject.XTCImageObject;
import com.xtc.shareapi.share.shareobject.XTCVideoObject;
import com.xtc.shareapi.share.shareobject.XTCWebObject;
import com.xtc.shareapi.share.sharescene.Chat;
import com.xtc.shareapi.share.utils.ShareUtil;
import com.xtc.shareapi.share.view.PopWindowManager;
import com.xtc.system.account.WatchAccountBase;
import com.xtc.utils.system.WatchModelUtil;

/**
 * 分享策略 v2：支持微聊/好友圈各自的分享入口、静默分享与时光记忆分享。
 */
public class StrategyVersionTwo extends ShareStrategy {

    StrategyVersionTwo(Context context, String appName, byte[] appIcon) {
        super(context, appName, appIcon);
    }

    @Override
    public void share(SendMessageToXTC.Request request, String appKey) {
        Scene scene = request.getScene();
        if (scene == null) {
            Log.d(TAG, "scene is null !");
            PopWindowManager.getInstance(context).showChooseSceneWindow(appKey, request, this);
            return;
        }
        final int installState = checkSceneInstall(scene.getType());
        if (installState != 0) {
            final Context shareContext = context;
            ShareHandlerUtil.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (installState == 1) {
                        Toast.makeText(shareContext, shareContext.getString(R.string.please_install_weichat), Toast.LENGTH_SHORT).show();
                        ShareUtil.startTargetActivity(shareContext, 13, "not install weichat!");
                    } else {
                        Toast.makeText(shareContext, shareContext.getString(R.string.please_install_moment), Toast.LENGTH_SHORT).show();
                        ShareUtil.startTargetActivity(shareContext, 14, "not install moment!");
                    }
                }
            });
            return;
        }
        if (!checkShareVersion(request.getMessage().getShareObject(), scene.getType())) {
            Log.d(TAG, "check sdk version fail");
            ShareUtil.startTargetActivity(context, 5, "current host not support share!");
            return;
        }
        Intent shareIntent = getShareIntentFromRequest(appKey, request);
        if (shareIntent == null) {
            Log.e(TAG, "get share intent error!");
            ShareUtil.startTargetActivity(context, 6, "get share intent error!");
            return;
        }
        ModuleSwitch moduleSwitch = getModuleSwitch(context, scene.getType());
        if (moduleSwitch != null && !moduleSwitch.isModule()) {
            Log.e(TAG, "current moduleSwitch is not open !");
            ShareUtil.startTargetActivity(context, 10, "current moduleSwitch is not open!");
            return;
        }
        Log.d(TAG, "the scene is " + scene.getType());
        shareIntent.putExtra(OpenApiConstant.IntentConstant.INTENT_APP_TOKEN, getTokenFromXTC(context, scene).getToken());
        context.startActivity(shareIntent);
    }

    /** 静默分享到微聊会话。 */
    public void silentlyShare(SendMessageToXTC.Request request, int requestCode, String packageName,
                              IShareCallback callback, String appKey) throws RemoteException {
        Chat chat = new Chat();
        request.setScene(chat);
        Intent shareIntent = getShareIntentFromRequest(appKey, request);
        if (shareIntent == null) {
            Log.e(TAG, "get share intent error!");
            callback.onResult(6, "get share intent error!");
            return;
        }
        ModuleSwitch moduleSwitch = getModuleSwitch(context, chat.getType());
        if (moduleSwitch != null && !moduleSwitch.isModule()) {
            Log.d(TAG, "current moduleSwitch is open  = " + moduleSwitch.isModule());
            callback.onResult(10, "this scene has bean forbid!");
            return;
        }
        AppInfo appInfo = new AppInfo();
        Log.d(TAG, "get app info = " + appInfo);
        shareIntent.putExtra(OpenApiConstant.IntentConstant.INTENT_APP_TOKEN, appInfo.getToken());
        bindServiceAndShare(requestCode, packageName, shareIntent, callback);
    }

    /** 静默分享到时光记忆。 */
    public void silentlyShareByTimeMemory(final SendMessageToXTC.Request request, String appKey,
                                          final ISilentlyShareCallback callback) throws RemoteException {
        ModuleSwitch moduleSwitch = getModuleSwitch(context, request.getScene().getType());
        if (moduleSwitch != null && !moduleSwitch.isModule()) {
            Log.d(TAG, "current moduleSwitch is open  = " + moduleSwitch.isModule());
            callback.onResult(10, "this scene has bean forbid!");
            return;
        }
        Intent serviceIntent = new Intent(OpenApiConstant.IntentConstant.SERVICE_TIME_MEMORY);
        serviceIntent.setPackage(OpenApiConstant.App.PACKAGE_TIME_MEMORY);
        final Context shareContext = context;
        context.bindService(serviceIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected : " + name);
                try {
                    IShareToTimeMemory.Stub.asInterface(service)
                            .sharePicture(((XTCImageObject) request.getMessage().getShareObject()).getImagePath(), callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d(TAG, "share to chat error = " + e);
                    try {
                        callback.onResult(100, "share to chat remote exception!");
                    } catch (RemoteException inner) {
                        inner.printStackTrace();
                        Log.d(TAG, "response result exception = " + e);
                    }
                } finally {
                    shareContext.unbindService(this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected : " + name);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void bindServiceAndShare(final int requestCode, final String packageName, final Intent shareIntent,
                                     final IShareCallback callback) {
        Intent serviceIntent = new Intent(OpenApiConstant.IntentConstant.INTENT_CHAT_SERVICE);
        serviceIntent.setPackage(OpenApiConstant.App.CHAT_PACKAGE_NAME);
        final Context shareContext = context;
        context.bindService(serviceIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                try {
                    Log.d(TAG, "share to service connect!");
                    IShareToChat.Stub.asInterface(service).shareToChat(shareIntent, requestCode, packageName, callback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d(TAG, "share to chat error = " + e);
                    try {
                        callback.onResult(100, "share to chat remote exception!");
                    } catch (RemoteException inner) {
                        inner.printStackTrace();
                        Log.d(TAG, "response result exception = " + e);
                    }
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "bindServiceAndShare onServiceDisconnected ");
                shareContext.unbindService(this);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public ModuleSwitch getModuleSwitch(Context context, int sceneType) {
        ModuleSwitch moduleSwitch = new ModuleSwitch();
        if (sceneType == Scene.TYPE_MOMENT) {
            moduleSwitch.setModule(WatchAccountBase.queryModuleSwitchByBoolean(context,
                    OpenApiConstant.ModuleSwitch.MODULE_SWITCH_MOMENT, !WatchModelUtil.isOverseas()));
        } else if (sceneType == Scene.TYPE_TIME_MEMORY) {
            moduleSwitch.setModule(WatchAccountBase.queryModuleSwitchByBoolean(context,
                    OpenApiConstant.ModuleSwitch.MODULE_TIME_MEMORY, !WatchModelUtil.isOverseas()));
        } else {
            moduleSwitch.setModule(true);
        }
        return moduleSwitch;
    }

    @Override
    public AppInfo getTokenFromXTC(Context context, Scene scene) {
        AppInfo appInfo = new AppInfo();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Uri uri;
            if (scene.getType() == Scene.TYPE_CHAT) {
                uri = Uri.parse(OpenApiConstant.TokenConstant.SHARE_CHAT_URI);
            } else {
                uri = Uri.parse(OpenApiConstant.TokenConstant.SHARE_MOMENT_URI);
            }
            Cursor cursor = contentResolver.query(uri, null, OpenApiConstant.TokenConstant.QUERY_SELECTION,
                    new String[]{context.getPackageName()}, null);
            if (cursor == null) {
                Log.d(TAG, "cursor is null!");
                return appInfo;
            }
            if (cursor.moveToNext()) {
                setAppInfo(appInfo, cursor);
            }
            cursor.close();
            return appInfo;
        } catch (Exception e) {
            Log.e(TAG, "query app info error = " + e);
            return appInfo;
        }
    }

    @Override
    public boolean setClassName(Intent intent, Scene scene) {
        String targetClassName = getTargetClassName(scene.getType());
        if (TextUtils.isEmpty(targetClassName)) {
            Log.d(TAG, "get target class name error!");
            return false;
        }
        intent.setClassName(scene.getPackageName(), targetClassName);
        return true;
    }

    @Override
    public String getTargetClassName(int sceneType) {
        if (sceneType == Scene.TYPE_CHAT) {
            return ShareUtil.getTargetClassName(context, OpenApiConstant.App.CHAT_PACKAGE_NAME, OpenApiConstant.App.LAUNCHER_CHAT_ACTIVITY);
        }
        if (sceneType == Scene.TYPE_MOMENT) {
            return ShareUtil.getTargetClassName(context, OpenApiConstant.App.MOMENT_PACKAGE_NAME, OpenApiConstant.App.LAUNCHER_MOMENT_ACTIVITY);
        }
        if (sceneType == Scene.TYPE_YOUTUBE) {
            return ShareUtil.getTargetClassName(context, OpenApiConstant.App.CHAT_PACKAGE_NAME, OpenApiConstant.App.LAUNCHER_CHAT_ACTIVITY);
        }
        return null;
    }

    @Override
    public boolean checkBaseVersion(int sceneType) {
        return !TextUtils.isEmpty(getTargetClassName(sceneType));
    }

    /** 校验宿主应用版本是否支持当前分享内容。 */
    public boolean checkShareVersion(IShareObject shareObject, int sceneType) {
        int hostSdkVersion;
        if (sceneType == Scene.TYPE_CHAT) {
            hostSdkVersion = ShareUtil.getHostSdkVersion(context, OpenApiConstant.App.CHAT_PACKAGE_NAME);
            Log.d(TAG, "THE CHAT IS" + hostSdkVersion);
        } else if (sceneType == Scene.TYPE_MOMENT) {
            hostSdkVersion = ShareUtil.getHostSdkVersion(context, OpenApiConstant.App.MOMENT_PACKAGE_NAME);
            Log.d(TAG, "THE TYPE_MOMENT IS" + hostSdkVersion);
        } else {
            return false;
        }
        if (shareObject instanceof XTCVideoObject) {
            Log.d(TAG, "XTCVideoObject sdkVersion" + hostSdkVersion);
            return hostSdkVersion > 2;
        }
        if (!(shareObject instanceof XTCWebObject)) {
            return true;
        }
        Log.d(TAG, "XTCWebObject sdkVersion" + hostSdkVersion);
        return hostSdkVersion > 2;
    }

    /** 检查目标场景是否已安装。 */
    public int checkSceneInstall(int sceneType) {
        if (sceneType == Scene.TYPE_CHAT) {
            return !ShareUtil.isInstallScene(context, OpenApiConstant.App.CHAT_PACKAGE_NAME) ? 1 : 0;
        }
        if (sceneType == Scene.TYPE_MOMENT) {
            return !ShareUtil.isInstallScene(context, OpenApiConstant.App.MOMENT_PACKAGE_NAME) ? 2 : 0;
        }
        return 0;
    }
}