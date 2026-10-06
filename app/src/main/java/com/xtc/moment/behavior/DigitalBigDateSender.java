package com.xtc.moment.behavior;

import android.content.Context;
import android.os.SystemClock;

import com.xtc.common.bigdata.BehaviorUtil;
import com.xtc.httplib.okhttp.WatchHttpResultException;
import com.xtc.log.LogUtil;
import com.xtc.log.util.TextUtils;
import com.xtc.moment.util.HandlerUtil;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLHandshakeException;

/**
 * 动态发布结果的大数据埋点上报。
 */
public class DigitalBigDateSender {

    private static final String TAG = "DigitalBigDateSender";
    private static final String MOMENT_PACKAGE_NAME = "com.xtc.moment";
    private static final String PUBLIC_SUCCESS = "200";
    private static final String PUBLIC_SUCCESS_STATUS = "1";
    private static final String PUBLIC_FAILE_STATUS = "2";

    private static int versionCode;

    public static void onPublicResult(final Context context, final DigitalEntity entity, final boolean success,
            final String failCode, final Throwable throwable) {
        if (context == null) {
            LogUtil.i(TAG, "onPublicResult, context is empty");
            return;
        }
        LogUtil.d(TAG, "onSendMsgResult：isSuccess = " + success + " entity = " + entity);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    uploadBuryingPointAsync(context, entity, success, failCode, throwable);
                } catch (Exception e) {
                    LogUtil.e(TAG, "uploadBuryingPoint error: ", e);
                }
            }
        });
    }

    public static void onUploadError(final Context context, final DigitalEntity entity, final String errorCode,
            final String failReason) {
        if (context == null) {
            LogUtil.i(TAG, "onPublicResult, context is empty");
            return;
        }
        LogUtil.d(TAG, "onSendMsgResult： entity = " + entity);
        HandlerUtil.runOnBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    entity.clientVersionCode = getVersionCode(context);
                    entity.code = errorCode;
                    entity.sendFailReason = failReason;
                    entity.publicStatus = PUBLIC_FAILE_STATUS;
                    uploadPublicMoment(context, entity);
                } catch (Exception e) {
                    LogUtil.e(TAG, "uploadBuryingPoint error: ", e);
                }
            }
        });
    }

    private static void uploadBuryingPointAsync(Context context, DigitalEntity entity, boolean success, String failCode,
            Throwable throwable) {
        entity.clientVersionCode = getVersionCode(context);
        if (success) {
            entity.code = PUBLIC_SUCCESS;
            entity.publicStatus = PUBLIC_SUCCESS_STATUS;
            setTotalTime(entity, SystemClock.elapsedRealtime());
            uploadPublicMoment(context, entity);
            return;
        }
        entity.code = failCode;
        entity.sendFailReason = getFailReason(throwable);
        entity.publicStatus = PUBLIC_FAILE_STATUS;
        uploadPublicMoment(context, entity);
    }

    private static String getFailReason(Throwable throwable) {
        if (throwable == null) {
            return DigitalConstant.ErrorCode.UNKNOWN_ERROR;
        }
        String message = throwable.getMessage();
        if (TextUtils.isEmpty(message)) {
            return DigitalConstant.ErrorCode.UNKNOWN_ERROR;
        }
        if (throwable instanceof WatchHttpResultException) {
            return ((WatchHttpResultException) throwable).serverCode();
        }
        if (message.contains("000008")) {
            return "000008";
        }
        return isTimeOutException(throwable, message) ? "9005" : filterHttpError(message);
    }

    private static boolean isTimeOutException(Throwable throwable, String message) {
        return (throwable instanceof SocketTimeoutException)
                || message.contains(SocketTimeoutException.class.getSimpleName())
                || (throwable instanceof SSLHandshakeException)
                || message.contains(SSLHandshakeException.class.getSimpleName())
                || (throwable instanceof ConnectException)
                || message.contains(ConnectException.class.getSimpleName())
                || (throwable instanceof ConnectTimeoutException)
                || message.contains(ConnectTimeoutException.class.getSimpleName())
                || message.toLowerCase().contains("timeout");
    }

    private static String filterHttpError(String message) {
        if (message.contains(DigitalConstant.MonitorError.MONITOR_ERROR_BOOT_LIMIT_TIME)) {
            return DigitalConstant.MonitorError.MONITOR_ERROR_BOOT_LIMIT_TIME;
        }
        if (message.contains("1002")) {
            return "1002";
        }
        if (message.contains("1003")) {
            return "1003";
        }
        if (message.contains(DigitalConstant.MonitorError.MONITOR_ERROR_HEAVY_TRAFFIC)) {
            return DigitalConstant.MonitorError.MONITOR_ERROR_HEAVY_TRAFFIC;
        }
        if (message.contains("1005")) {
            return "1005";
        }
        return message.contains(DigitalConstant.MonitorError.MONITOR_ERROR_NO_NETWORK_PERMISSION)
                ? DigitalConstant.MonitorError.MONITOR_ERROR_NO_NETWORK_PERMISSION
                : DigitalConstant.ErrorCode.UNKNOWN_ERROR;
    }

    private static String getVersionCode(Context context) {
        if (versionCode == 0) {
            try {
                versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            } catch (Exception e) {
                LogUtil.e(TAG, "getVersionCode error : ", e);
            }
        }
        return String.valueOf(versionCode);
    }

    private static void setTotalTime(DigitalEntity entity, long endTime) {
        if (entity.startPushTime != 0) {
            entity.totalTime = String.valueOf(endTime - entity.startPushTime);
            return;
        }
        long totalTime = TextUtils.isEmpty(entity.compressTime) ? 0L : 0L + Long.parseLong(entity.compressTime);
        if (!TextUtils.isEmpty(entity.getTokenTime)) {
            totalTime += Long.parseLong(entity.getTokenTime);
        }
        if (!TextUtils.isEmpty(entity.uploadTime)) {
            totalTime += Long.parseLong(entity.uploadTime);
        }
        if (!TextUtils.isEmpty(entity.publicTime)) {
            totalTime += Long.parseLong(entity.publicTime);
        }
        if (!TextUtils.isEmpty(entity.transformTime)) {
            totalTime += Long.parseLong(entity.transformTime);
        }
        entity.totalTime = String.valueOf(totalTime);
    }

    public static void uploadPublicMoment(Context context, DigitalEntity entity) {
        LogUtil.i(TAG, "uploadPublicMoment, " + entity);
        BehaviorUtil.customEvent(context, DigitalEntity.FACTION_NAME, entity.getHashMap());
    }
}