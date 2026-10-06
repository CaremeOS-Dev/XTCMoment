package com.xtc.ui.widget.toast.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import com.xtc.log.LogUtil;
import java.lang.reflect.Field;

/** Toast 工具：复用同一个 Toast 实例，并 hook 内部 Handler 避免异常崩溃。 */
public class ToastUtil {
    private static final String TAG = "ToastUtil";
    private static Field sFieldTN;
    private static Field sFieldTNHandler;
    private static Handler mainHandler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;

    static {
        try {
            sFieldTN = Toast.class.getDeclaredField("mTN");
            sFieldTN.setAccessible(true);
            sFieldTNHandler = sFieldTN.getType().getDeclaredField("mHandler");
            sFieldTNHandler.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void hook(Toast target) {
        try {
            LogUtil.i(TAG, "Toast Hook");
            Object tn = sFieldTN.get(target);
            sFieldTNHandler.set(tn, new SafelyHandlerWrapper((Handler) sFieldTNHandler.get(tn)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 包裹原始 Handler，吞掉分发过程中的异常。 */
    private static class SafelyHandlerWrapper extends Handler {
        private final Handler impl;

        SafelyHandlerWrapper(Handler handler) {
            this.impl = handler;
        }

        @Override
        public void dispatchMessage(Message message) {
            try {
                super.dispatchMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message message) {
            this.impl.handleMessage(message);
        }
    }

    public static void showShortCover(Context context, String message) {
        showCover(context, message, 0);
    }

    public static void showLongCover(Context context, String message) {
        showCover(context, message, 1);
    }

    private static void showCover(final Context context, final String message, final int duration) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            showCoverImpl(context, message, duration);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showCoverImpl(context, message, duration);
                }
            });
        }
    }

    private static void showCoverImpl(Context context, String message, int duration) {
        try {
            if (toast != null) {
                toast.setText(message);
                toast.setDuration(duration);
                LogUtil.d(TAG, "showCover: " + toast.hashCode() + ";msg = " + message);
                toast.show();
            } else {
                toast = Toast.makeText(context.getApplicationContext(), message, duration);
                hook(toast);
                LogUtil.d(TAG, "showCover: " + toast.hashCode() + ";msg = " + message);
                toast.show();
            }
        } catch (Exception unused) {
            toast = null;
        }
    }
}