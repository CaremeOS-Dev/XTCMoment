package com.xtc.web.core.manager;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.xtc.log.LogUtil;
import com.xtc.moment.module.Constants;
import com.xtc.ui.widget.button.DoubleFlatButton;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnWithTitleBean;
import com.xtc.ui.widget.util.DialogUtil;
import com.xtc.web.core.CoreConstants;
import com.xtc.moment.R;
import com.xtc.web.core.callback.CompletionHandler;
import com.xtc.web.core.data.req.ReqJsStartApp;
import com.xtc.web.core.data.resp.RespAppState;
import com.xtc.web.core.data.resp.RespStartApp;
import com.xtc.web.core.jump.AppDownloadDescriptionDialog;
import com.xtc.web.core.jump.JumpManager;
import com.xtc.web.core.provider.AppStateHelper;
import com.xtc.web.core.provider.JumpContentProvider;
import com.xtc.web.core.utils.JSONUtil;
import com.xtc.web.core.utils.WebUtils;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

/** H5 打开第三方应用：先查 launcher 的安装/展示状态，再决定跳转、提示或拉起下载引导。 */
public class JsJumpAppUtils {

    private static final String TAG = CoreConstants.TAG + JsJumpAppUtils.class.getSimpleName();
    private static final String LAUNCHER_URI = "content://com.xtc.i3launcher/app";
    private static final String JUMP_RESULT_PATH = "/jumpActivityResult";
    private static ContentObserver contentObserver;
    private static ContentResolver contentResolver;

    /** H5 请求打开应用。 */
    public static void startApp(final Context context, final ReqJsStartApp reqJsStartApp,
            final CompletionHandler<RespStartApp> completionHandler) {
        if (CoreConstants.JumpConstant.THEME_DIAL_URI.equals(reqJsStartApp.getUri())
                && !WebUtils.isInstallApp(context, CoreConstants.JumpConstant.COM_XTC_THEME)) {
            reqJsStartApp.setPackageName(CoreConstants.JumpConstant.COM_XTC_DIALSTORE);
            reqJsStartApp.setTargetActivity(CoreConstants.JumpConstant.COM_XTC_DIALSTORE_TARGET_ACTIVITY);
            reqJsStartApp.setUri(CoreConstants.JumpConstant.DIALSTORE_URI);
        }
        contentResolver = context.getApplicationContext().getContentResolver();
        Uri launcherUri = Uri.parse(LAUNCHER_URI);
        final Uri jumpResultUri = Uri.parse(Constants.ProviderConstants.PREFIX_CONTENT_PROVIDER
                + JumpContentProvider.getFileProviderName(context) + JUMP_RESULT_PATH);
        RespStartApp response = queryLauncherState(context, reqJsStartApp, launcherUri);
        try {
            PackageManager packageManager = context.getPackageManager();
            packageManager.getPackageInfo(context.getPackageName(), 0);
            packageManager.getPackageInfo(reqJsStartApp.getPackageName(), 0);
        } catch (Exception e) {
            LogUtil.e(TAG, "get app error = ", e);
            response = new RespStartApp();
            response.setCode(RespStartApp.Code.NOT_INSTALL);
        }
        int isForResult = reqJsStartApp.getIsForResult();
        if (response == null) {
            try {
                if (isForResult == 0) {
                    LogUtil.d(TAG, " NOT FOR RESULT");
                    Intent intent = Intent.parseUri(reqJsStartApp.getUri(), reqJsStartApp.getFlag());
                    if (reqJsStartApp.getIntentFlag() != 0) {
                        intent.setFlags(reqJsStartApp.getIntentFlag());
                    }
                    if (reqJsStartApp.getCategory() != null) {
                        intent.addCategory(reqJsStartApp.getCategory());
                    }
                    context.startActivity(intent);
                    response = new RespStartApp();
                    response.setCode(RespStartApp.Code.SUCCESS);
                } else {
                    Intent intent = Intent.parseUri(reqJsStartApp.getUri(), reqJsStartApp.getFlag());
                    if (reqJsStartApp.getIntentFlag() != 0) {
                        intent.setFlags(reqJsStartApp.getIntentFlag());
                    }
                    if (reqJsStartApp.getCategory() != null) {
                        intent.addCategory(reqJsStartApp.getCategory());
                    }
                    final RespStartApp resultResponse = new RespStartApp();
                    resultResponse.setCode(RespStartApp.Code.SUCCESS);
                    LogUtil.d(TAG, "IS FOR RESULT");
                    String requestKey = UUID.randomUUID().toString();
                    if (contentObserver != null) {
                        contentResolver.unregisterContentObserver(contentObserver);
                        contentObserver = null;
                    }
                    contentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
                        @Override
                        public void onChange(boolean selfChange, Uri uri) {
                            super.onChange(selfChange, uri);
                            LogUtil.i(TAG, "onActivityResult");
                            Cursor cursor = contentResolver.query(jumpResultUri, null, null, null, null);
                            if (cursor == null) {
                                LogUtil.w(TAG, "cursorJump is null");
                                return;
                            }
                            if (cursor.moveToNext()) {
                                HashMap map = JSONUtil.fromJSON(cursor.getString(0), HashMap.class);
                                if (map == null) {
                                    LogUtil.w(TAG, "remote data is null");
                                    cursor.close();
                                    return;
                                }
                                if (map.containsKey("data")) {
                                    String data = (String) map.get("data");
                                    LogUtil.d(TAG, "data is " + data);
                                    resultResponse.setData(data);
                                    completionHandler.complete(resultResponse);
                                }
                            }
                            cursor.close();
                        }
                    };
                    contentResolver.registerContentObserver(jumpResultUri, true, contentObserver);
                    JumpManager.start(context, intent, requestKey, 100);
                    response = resultResponse;
                }
            } catch (ActivityNotFoundException e) {
                LogUtil.e(TAG, "activity not found ->", e);
                response = new RespStartApp();
                response.setCode(RespStartApp.Code.NOT_UPDATE);
            } catch (URISyntaxException e) {
                LogUtil.e(TAG, "uri parse error ->", e);
                response = new RespStartApp();
                response.setCode(RespStartApp.Code.URI_ERROR);
                response.setDesc("uri parse error = " + e);
            }
        } else if (RespStartApp.Code.NOT_INSTALL.equals(response.getCode())
                && ReqJsStartApp.ShowNewUIType.SHOW == reqJsStartApp.getIsShowNewUI()) {
            boolean hasAppInfoProvider = AppStateHelper.getInstance(context).checkHasAppInfoProvider();
            LogUtil.d(TAG, "isHasAppInfoProvider = " + hasAppInfoProvider);
            if (hasAppInfoProvider) {
                AppStateHelper.getInstance(context).registerObserver();
                AppStateHelper.getInstance(context).setAppStateListener(new AppStateHelper.AppStateListener() {
                    @Override
                    public void onResultState(RespAppState respAppState) {
                        if (respAppState != null) {
                            LogUtil.d(TAG, "respAppState = [" + respAppState.toString() + "]");
                            if (RespAppState.Code.CARRIAGE == respAppState.getCode()) {
                                showInstallDialog(context, reqJsStartApp.getPackageName(),
                                        respAppState.getData().getName());
                            } else if (RespAppState.Code.UNDERCARRIAGE == respAppState.getCode()) {
                                JsToastManager.showToast(context, context.getString(R.string.str_version_no_support));
                            }
                            return;
                        }
                        JsToastManager.showToast(context, context.getString(R.string.str_remote_error));
                    }
                });
                AppStateHelper.getInstance(context).getAppState(reqJsStartApp.getPackageName());
            } else {
                JsToastManager.showToast(context, context.getString(R.string.str_version_no_support));
            }
        }
        if (isForResult == 0 || !RespStartApp.Code.SUCCESS.equals(response.getCode())) {
            completionHandler.complete(response);
        }
    }

    /** 查询 launcher 中该应用的开启/展示状态。 */
    private static RespStartApp queryLauncherState(Context context, ReqJsStartApp reqJsStartApp, Uri launcherUri) {
        Cursor cursor = null;
        RespStartApp response;
        try {
            if (reqJsStartApp.getTargetActivity() != null) {
                cursor = contentResolver.query(launcherUri, null, "packageName=? and targetActivity=?",
                        new String[]{reqJsStartApp.getPackageName(), reqJsStartApp.getTargetActivity()}, null);
            } else {
                cursor = contentResolver.query(launcherUri, null, "packageName=?",
                        new String[]{reqJsStartApp.getPackageName()}, null);
            }
            if (cursor != null && cursor.moveToNext()) {
                int isOpen = cursor.getInt(cursor.getColumnIndex("isOpen"));
                int isDisplay = cursor.getInt(cursor.getColumnIndex("isDisplay"));
                if (isOpen == 0) {
                    response = new RespStartApp();
                    response.setCode(RespStartApp.Code.FORBID);
                } else if (isDisplay == 0 && reqJsStartApp.getIgnoreDisplay() == 0) {
                    response = new RespStartApp();
                    response.setCode(RespStartApp.Code.NOT_DISPLAY);
                } else {
                    response = null;
                }
            } else {
                response = new RespStartApp();
                response.setCode(RespStartApp.Code.NOT_INSTALL);
            }
        } catch (Throwable throwable) {
            if (cursor != null) {
                cursor.close();
            }
            throw throwable;
        }
        if (cursor != null) {
            cursor.close();
        }
        return response;
    }

    /** 弹出“去应用市场下载”引导弹窗。 */
    public static void showInstallDialog(final Context context, final String packageName, String appName) {
        DoubleFlatBtnWithTitleBean bean = new DoubleFlatBtnWithTitleBean(context, true,
                context.getString(R.string.app_download_description),
                context.getString(R.string.app_download_content, appName),
                R.string.str_cancel_download, R.string.str_go_download);
        final AppDownloadDescriptionDialog dialog = new AppDownloadDescriptionDialog(context,
                bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        DoubleFlatButton bottomBtn = dialog.getBottomBtn();
        bottomBtn.getLeftButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismissDialog(dialog);
            }
        });
        bottomBtn.getRightArea().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtil.dismissDialog(dialog);
                try {
                    context.startActivity(Intent.parseUri(Constants.State.APP_UPDATE_DATA + packageName, 0));
                } catch (URISyntaxException e) {
                    LogUtil.e(TAG, "start target Activity ->", e);
                }
            }
        });
        DialogUtil.showDialog(dialog);
    }

    /** 注销跳转结果监听。 */
    public static void unRegisterCallback() {
        ContentObserver observer = contentObserver;
        if (observer != null) {
            contentResolver.unregisterContentObserver(observer);
            contentResolver = null;
            contentObserver = null;
            LogUtil.d(TAG, "release contentProvider");
        }
    }
}