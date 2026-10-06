package com.xtc.ui.widget.selfstart;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.xtc.log.LogUtil;
import java.util.concurrent.Callable;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/** 自启动权限检查与引导工具。 */
public class SelfStartUtils {
    private static final String TAG = "SelfStartUtils";
    private static boolean isTargetActivityExist;

    public static void checkSelfStartReminded(Context context, String contentText) {
        checkSelfStartReminded(context, context.getPackageName(), contentText, null);
    }

    public static void checkSelfStartReminded(Context context, String packageName, String contentText) {
        checkSelfStartReminded(context, packageName, contentText, null);
    }

    public static void checkSelfStartReminded(final Context context, final String packageName, final String contentText,
                                              final Action1<Boolean> callback) {
        Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (!SelfStartUtils.isHadSelfStartPermission(context, packageName)) {
                    return Boolean.valueOf(SelfStartUtils.isTargetActivityExist(context));
                }
                LogUtil.d(SelfStartUtils.TAG, packageName + " 有权限或当前桌面/设置版本不支持自启动");
                return false;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable throwable) {
                LogUtil.e(SelfStartUtils.TAG, "checkSelfStartReminded error: ", throwable);
                if (callback != null) {
                    callback.call(false);
                }
            }

            @Override
            public void onNext(Boolean shouldRemind) {
                if (shouldRemind.booleanValue()) {
                    context.startActivity(new Intent(context, (Class<?>) SelfStartActivity.class)
                            .putExtra(SelfStartConstant.EXTRA_CONTENT_TEXT, contentText)
                            .putExtra(SelfStartConstant.EXTRA_PACKAGE_NAME, packageName));
                }
                if (callback != null) {
                    callback.call(shouldRemind);
                }
            }
        });
    }

    public static boolean isHadSelfStartPermission(Context context, String packageName) {
        try {
            Bundle result = context.getContentResolver().call(
                    Uri.parse(SelfStartConstant.LAUNCHER_SELF_START_URI),
                    SelfStartConstant.METHOD_GET_PACKAGE, packageName, (Bundle) null);
            if (result != null) {
                return result.getBoolean(SelfStartConstant.EXTRA_SELF_START_PACKAGE);
            }
            return true;
        } catch (Exception e) {
            LogUtil.e(TAG, "检查应用是否有自启权限,error = " + e.getMessage());
            return true;
        }
    }

    private static boolean isTargetActivityExist(Context context) {
        if (isTargetActivityExist) {
            return true;
        }
        Intent intent = new Intent();
        intent.setAction(SelfStartConstant.ACTION_SETTINGS_SELF_START);
        isTargetActivityExist = context.getPackageManager().resolveActivity(intent, 65536) != null;
        return isTargetActivityExist;
    }
}