package com.xtc.ui.widget.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.ui.widget.dialog.DoubleFlatBtnDialog;
import com.xtc.ui.widget.dialog.DoubleFlatBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.DoubleLongBtnDialog;
import com.xtc.ui.widget.dialog.EditableDoubleFlatBtnDialog;
import com.xtc.ui.widget.dialog.LongHollowBtnDialog;
import com.xtc.ui.widget.dialog.LongHollowBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.LongSolidBtnDialog;
import com.xtc.ui.widget.dialog.LongSolidBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.NormalIconDialog;
import com.xtc.ui.widget.dialog.ScrollableDoubleFlatBtnWithTitleDialog;
import com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnBean;
import com.xtc.ui.widget.dialog.bean.icon.DoubleIconBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.icon.NormalIconDialogBean;
import com.xtc.ui.widget.dialog.bean.icon.ThreeIconBtnBean;
import com.xtc.ui.widget.dialog.bean.icon.ThreeIconBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.noIcon.CtaPermissionBean;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleFlatBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.noIcon.DoubleLongBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongHollowBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongHollowBtnWithTitleBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongSolidBtnBean;
import com.xtc.ui.widget.dialog.bean.noIcon.LongSolidBtnWithTitleBean;
import com.xtc.ui.widget.permission.cta.CtaPermissionDialog;
import com.xtc.ui.widget.permission.cta.interfaces.ICtaPermissionCallback;
import com.xtc.ui.widget.privacy.IPrivacyDialogListener;
import com.xtc.ui.widget.privacy.PrivacyBean;
import com.xtc.ui.widget.privacy.PrivacyFirstDialog;

import java.util.Map;

/** Factory helpers that build and configure every dialog in the widget library. */
public class DialogUtil {

    private static String TAG = "DialogUtil";

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
        dialog.dismiss();
    }

    public static NormalIconDialog makeDoubleIconBtnDialog(Context context, DoubleIconBtnBean bean) {
        if (bean.isCenterInVertical()) {
            return makeNormalDialog(NormalIconDialog.DOUBLE_BUTTON, bean.isForbidSwipeToDismiss(), context, null, bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(), bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), 0, bean.getListener());
        }
        return makeNormalDialog(NormalIconDialog.DOUBLE_BUTTON_WITH_TITLE, bean.isForbidSwipeToDismiss(), context, null, bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(), bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), 0, bean.getListener());
    }

    public static NormalIconDialog makeDoubleIconBtnWithTitleDialog(Context context, DoubleIconBtnWithTitleBean bean) {
        return makeNormalDialog(NormalIconDialog.DOUBLE_BUTTON_WITH_TITLE, bean.isForbidSwipeToDismiss(), context, bean.getTitleString(), bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(), bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), 0, bean.getListener());
    }

    public static NormalIconDialog makeThreeIconBtnDialog(Context context, ThreeIconBtnBean bean) {
        return makeNormalDialog(NormalIconDialog.THREE_BUTTON, bean.isForbidSwipeToDismiss(), context, null, bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(), bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), bean.getBottomStringId(), bean.getListener());
    }

    public static NormalIconDialog makeThreeIconBtnDialogAll(Context context, ThreeIconBtnBean bean) {
        return makeNormalDialogAll(NormalIconDialog.THREE_BUTTON, bean.isForbidSwipeToDismiss(), context, null, bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(), bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), bean.getBottomStringId(), bean.getListener());
    }

    public static NormalIconDialog makeThreeIconBtnWithTitleDialog(Context context, ThreeIconBtnWithTitleBean bean) {
        return makeNormalDialog(NormalIconDialog.THREE_BUTTON_WITH_TITLE, bean.isForbidSwipeToDismiss(), context, bean.getTitleString(), bean.getLeftBtnBgColorIds(), bean.getLeftIconConstants(), bean.getLeftStringId(), bean.getRightBtnBgColorIds(), bean.getRightIconConstants(), bean.getRightStringId(), bean.getBottomStringId(), bean.getListener());
    }

    private static NormalIconDialog makeNormalDialog(int style, boolean forbidSwipeToDismiss, Context context, CharSequence title, int[] leftColors, int leftIcon, int leftStringId, int[] rightColors, int rightIcon, int rightStringId, int bottomStringId, NormalIconDialogBean.OnClickListener listener) {
        NormalIconDialogBean bean = new NormalIconDialogBean(style, forbidSwipeToDismiss, context, title, leftColors, leftIcon, leftStringId, rightColors, rightIcon, rightStringId, bottomStringId, listener);
        NormalIconDialog dialog = new NormalIconDialog(context, forbidSwipeToDismiss, false);
        dialog.initData(bean);
        return dialog;
    }

    private static NormalIconDialog makeNormalDialogAll(int style, boolean forbidSwipeToDismiss, Context context, CharSequence title, int[] leftColors, int leftIcon, int leftStringId, int[] rightColors, int rightIcon, int rightStringId, int bottomStringId, NormalIconDialogBean.OnClickListener listener) {
        NormalIconDialogBean bean = new NormalIconDialogBean(style, forbidSwipeToDismiss, context, title, leftColors, leftIcon, leftStringId, rightColors, rightIcon, rightStringId, bottomStringId, listener);
        NormalIconDialog dialog = new NormalIconDialog(context, forbidSwipeToDismiss, true);
        dialog.initData(bean);
        return dialog;
    }

    public static LongSolidBtnDialog makeLongSolidBtnDialog(Context context, LongSolidBtnBean bean) {
        LongSolidBtnDialog dialog = new LongSolidBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static LongSolidBtnWithTitleDialog makeLongSolidBtnWithTitleDialog(Context context, LongSolidBtnWithTitleBean bean) {
        LongSolidBtnWithTitleDialog dialog = new LongSolidBtnWithTitleDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static LongHollowBtnDialog makeLongHollowBtnDialog(Context context, LongHollowBtnBean bean) {
        LongHollowBtnDialog dialog = new LongHollowBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static LongHollowBtnWithTitleDialog makeLongHollowBtnWithTitleDialog(Context context, LongHollowBtnWithTitleBean bean) {
        LongHollowBtnWithTitleDialog dialog = new LongHollowBtnWithTitleDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static DoubleFlatBtnDialog makeDoubleFlatBtnDialog(Context context, DoubleFlatBtnBean bean) {
        DoubleFlatBtnDialog dialog = new DoubleFlatBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static EditableDoubleFlatBtnDialog makeEditableDoubleFlatBtnDialog(Context context, DoubleFlatBtnBean bean) {
        EditableDoubleFlatBtnDialog dialog = new EditableDoubleFlatBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static DoubleFlatBtnWithTitleDialog makeDoubleFlatBtnWithTitleDialog(Context context, DoubleFlatBtnWithTitleBean bean) {
        DoubleFlatBtnWithTitleDialog dialog = new DoubleFlatBtnWithTitleDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static DoubleLongBtnDialog makeDoubleLongBtnDialog(Context context, DoubleLongBtnBean bean) {
        DoubleLongBtnDialog dialog = new DoubleLongBtnDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static ScrollableDoubleFlatBtnWithTitleDialog makeScrollableDoubleFlatBtnWithTitleDialog(Context context, DoubleFlatBtnWithTitleBean bean) {
        ScrollableDoubleFlatBtnWithTitleDialog dialog = new ScrollableDoubleFlatBtnWithTitleDialog(context, bean.isForbidSwipeToDismiss());
        dialog.initData(bean);
        return dialog;
    }

    public static CtaPermissionDialog makeCtaPermissionDialog(Context context, CtaPermissionBean bean, ICtaPermissionCallback callback) {
        if (context == null || callback == null) {
            LogUtil.d(TAG, "context == null || callback == null");
            return null;
        }
        CtaPermissionDialog dialog = new CtaPermissionDialog(context, true);
        dialog.initData(bean);
        dialog.setCallBack(callback);
        return dialog;
    }

    public static Dialog makePrivacyDialog(Context context, String packageName, IPrivacyDialogListener listener) {
        return makePrivacyDialog(context, packageName, "", listener);
    }

    public static Dialog makePrivacyDialog(Context context, IPrivacyDialogListener listener) {
        return makePrivacyDialog(context, context.getPackageName(), "", listener);
    }

    public static Dialog makePrivacyDialog(Context context, String packageName, String title, IPrivacyDialogListener listener) {
        return makePrivacyDialog(context, packageName, title, null, listener);
    }

    public static Dialog makePrivacyDialog(Context context, String packageName, String title, String appName, IPrivacyDialogListener listener) {
        return makePrivacyDialog(context, packageName, title, appName, null, listener);
    }

    public static Dialog makePrivacyDialog(Context context, String packageName, String title, String appName, Map<String, String> reasonMap, IPrivacyDialogListener listener) {
        if (context == null || listener == null || title == null) {
            LogUtil.d(TAG, "context == null || callback == null || title == null");
            return null;
        }
        if (TextUtils.isEmpty(packageName)) {
            packageName = context.getPackageName();
        }
        PrivacyBean privacy = CtaPermissionUtil.getPrivacy(context, packageName, reasonMap);
        privacy.setFirstTitle(title);
        if (!TextUtils.isEmpty(appName)) {
            privacy.setAppName(appName);
        }
        LogUtil.i(TAG, "makePrivacyDialog() context = [" + context + "], packageName = [" + packageName + "], privacyBean = [" + privacy + "]");
        PrivacyFirstDialog dialog = new PrivacyFirstDialog(context);
        dialog.setListener(listener);
        dialog.init(privacy);
        return dialog;
    }

    public static CtaPermissionDialog makeCtaPermissionDialog(Context context, ICtaPermissionCallback callback) {
        if (context == null || callback == null) {
            LogUtil.d(TAG, "context == null || callback == null");
            return null;
        }
        return makeCtaPermissionDialog(context, new CtaPermissionBean(CtaPermissionUtil.getAuthorityInfos(context, context.getPackageName()), getApplicationName(context)), callback);
    }

    public static String getApplicationName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(context.getPackageName(), 128)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
