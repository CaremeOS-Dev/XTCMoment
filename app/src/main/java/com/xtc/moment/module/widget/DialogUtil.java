package com.xtc.moment.module.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;

import com.xtc.log.LogUtil;
import com.xtc.ui.widget.dialog.DoubleFlatBtnDialog;
import com.xtc.ui.widget.dialog.DoubleFlatBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.LongHollowBtnDialog;
import com.xtc.ui.widget.dialog.LongHollowBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.LongSolidBtnDialog;
import com.xtc.ui.widget.dialog.LongSolidBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.icon.NormalIconDialogBean;
import com.xtc.ui.widget.dialog.bean.icon.ThreeIconBtnBean;
import com.xtc.ui.widget.dialog.bean.icon.ThreeIconBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongHollowBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongHollowBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongSolidBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongSolidBtnWithTitleBean;

/**
 * 动态模块弹窗工厂与安全显示/关闭工具。
 */
public class DialogUtil {

    private static final String TAG = "DialogUtil";

    public static boolean isDialogShowing(Dialog dialog) {
        return dialog != null && dialog.isShowing();
    }

    public static void showDialog(Dialog dialog) {
        LogUtil.d(TAG, "showDialog --> " + dialog);
        if (dialog == null || dialog.isShowing()) {
            return;
        }
        dialog.show();
    }

    public static void dismissDialog(Dialog dialog) {
        LogUtil.d(TAG, "dismissDialog --> " + dialog);
        if (dialog == null || !dialog.isShowing()) {
            return;
        }
        Context baseContext = ((ContextWrapper) dialog.getContext()).getBaseContext();
        if (!(baseContext instanceof Activity) || ((Activity) baseContext).isFinishing()) {
            return;
        }
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "dismissDialog : ", e);
        }
    }

    public static void cancelDialog(Dialog dialog) {
        LogUtil.d(TAG, "dismissDialog --> " + dialog);
        if (dialog == null || !dialog.isShowing()) {
            return;
        }
        Context baseContext = ((ContextWrapper) dialog.getContext()).getBaseContext();
        if (!(baseContext instanceof Activity)) {
            return;
        }
        Activity activity = (Activity) baseContext;
        if (activity.isDestroyed() || activity.isFinishing()) {
            return;
        }
        try {
            dialog.cancel();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(TAG, "cancelDialog : ", e);
        }
    }

    public static NormalIconDialog makeDoubleIconBtnDialog(Context context, DoubleIconBtnBean bean) {
        return makeNormalDialog(bean.isCenterInVertical() ? 0 : 1, bean.isForbidSwipeToDismiss(), context, null,
                bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(),
                bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), 0,
                bean.getListener());
    }

    public static NormalIconDialog makeDoubleIconBtnWithTitleDialog(Context context, DoubleIconBtnWithTitleBean bean) {
        return makeNormalDialog(1, bean.isForbidSwipeToDismiss(), context, bean.getTitleString(),
                bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(),
                bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), 0,
                bean.getListener());
    }

    public static NormalIconDialog makeThreeIconBtnDialog(Context context, ThreeIconBtnBean bean) {
        return makeNormalDialog(2, bean.isForbidSwipeToDismiss(), context, null, bean.getLeftBtnBgColorIds(),
                bean.getLeftIconConstants(), bean.getLeftStringId(), bean.getRightBtnBgColorIds(),
                bean.getRightIconConstants(), bean.getRightStringId(), bean.getBottomStringId(), bean.getListener());
    }

    public static NormalIconDialog makeThreeIconBtnWithTitleDialog(Context context, ThreeIconBtnWithTitleBean bean) {
        return makeNormalDialog(3, bean.isForbidSwipeToDismiss(), context, bean.getTitleString(),
                bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(),
                bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(),
                bean.getBottomStringId(), bean.getListener());
    }

    private static NormalIconDialog makeNormalDialog(int type, boolean forbidSwipeToDismiss, Context context,
            CharSequence title, int[] leftBgColors, int leftIcon, int leftStringId, int[] rightBgColors,
            int rightIcon, int rightStringId, int bottomStringId, NormalIconDialogBean.OnClickListener listener) {
        NormalIconDialogBean bean = new NormalIconDialogBean(type, forbidSwipeToDismiss, context, title, leftBgColors,
                leftIcon, leftStringId, rightBgColors, rightIcon, rightStringId, bottomStringId, listener);
        NormalIconDialog dialog = new NormalIconDialog(context, forbidSwipeToDismiss);
        dialog.initData(bean);
        return dialog;
    }

    public static LongSolidBtnDialog makeLongSolidBtnDialog(Context context, LongSolidBtnBean bean) {
        LongSolidBtnDialog dialog = new LongSolidBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static LongSolidBtnWithTitleDialog makeLongSolidBtnWithTitleDialog(Context context,
            LongSolidBtnWithTitleBean bean) {
        LongSolidBtnWithTitleDialog dialog = new LongSolidBtnWithTitleDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static LongHollowBtnDialog makeLongHollowBtnDialog(Context context, LongHollowBtnBean bean) {
        LongHollowBtnDialog dialog = new LongHollowBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static LongHollowBtnWithTitleDialog makeLongHollowBtnWithTitleDialog(Context context,
            LongHollowBtnWithTitleBean bean) {
        LongHollowBtnWithTitleDialog dialog =
                new LongHollowBtnWithTitleDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static DoubleFlatBtnDialog makeDoubleFlatBtnDialog(Context context, DoubleFlatBtnBean bean) {
        DoubleFlatBtnDialog dialog = new DoubleFlatBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static DoubleFlatBtnWithTitleDialog makeDoubleFlatBtnWithTitleDialog(Context context,
            DoubleFlatBtnWithTitleBean bean) {
        DoubleFlatBtnWithTitleDialog dialog =
                new DoubleFlatBtnWithTitleDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }
}