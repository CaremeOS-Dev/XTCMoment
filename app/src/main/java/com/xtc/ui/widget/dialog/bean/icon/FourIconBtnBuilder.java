package com.xtc.ui.widget.dialog.bean.icon;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import com.xtc.ui.widget.dialog.FourIconBtnDialog;
import com.xtc.ui.widget.dialog.bean.BaseDialogBean;

/** 四图标按钮对话框的构建参数。 */
public class FourIconBtnBuilder extends BaseDialogBean {
    private int[] bottomNegativeBtnBgColorIds;
    private int bottomNegativeIconConstants;
    private int bottomNegativeStringId;
    private int[] bottomPositiveBtnBgColorIds;
    private int bottomPositiveIconConstants;
    private int bottomPositiveStringId;
    private Context context;
    private boolean forbidSwipeToDismiss;
    private OnClickListener listener;
    private int[] topNegativeBtnBgColorIds;
    private int topNegativeIconConstants;
    private int topNegativeStringId;
    private int[] topPositiveBtnBgColorIds;
    private int topPositiveIconConstants;
    private int topPositiveStringId;

    /** 四个按钮的点击回调。 */
    public interface OnClickListener {
        void onBottomNegativeBtnClick(Dialog dialog, View view);

        void onBottomPositiveBtnClick(Dialog dialog, View view);

        void onTopNegativeBtnClick(Dialog dialog, View view);

        void onTopPositiveBtnClick(Dialog dialog, View view);
    }

    public FourIconBtnBuilder(Context context, OnClickListener listener) {
        this(context, true, listener);
    }

    public FourIconBtnBuilder(Context context, boolean forbidSwipeToDismiss, OnClickListener listener) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.listener = listener;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public FourIconBtnBuilder setTopPositiveBtn(int[] bgColorIds, int iconConstant, int stringId) {
        this.topPositiveBtnBgColorIds = bgColorIds;
        this.topPositiveIconConstants = iconConstant;
        this.topPositiveStringId = stringId;
        return this;
    }

    public FourIconBtnBuilder setTopNegativeBtn(int[] bgColorIds, int iconConstant, int stringId) {
        this.topNegativeBtnBgColorIds = bgColorIds;
        this.topNegativeIconConstants = iconConstant;
        this.topNegativeStringId = stringId;
        return this;
    }

    public FourIconBtnBuilder setBottomPositiveBtn(int[] bgColorIds, int iconConstant, int stringId) {
        this.bottomPositiveBtnBgColorIds = bgColorIds;
        this.bottomPositiveIconConstants = iconConstant;
        this.bottomPositiveStringId = stringId;
        return this;
    }

    public FourIconBtnBuilder setBottomNegativeBtn(int[] bgColorIds, int iconConstant, int stringId) {
        this.bottomNegativeBtnBgColorIds = bgColorIds;
        this.bottomNegativeIconConstants = iconConstant;
        this.bottomNegativeStringId = stringId;
        return this;
    }

    public int[] getTopPositiveBtnBgColorIds() {
        return this.topPositiveBtnBgColorIds;
    }

    public int getTopPositiveIconConstants() {
        return this.topPositiveIconConstants;
    }

    public int getTopPositiveStringId() {
        return this.topPositiveStringId;
    }

    public int[] getTopNegativeBtnBgColorIds() {
        return this.topNegativeBtnBgColorIds;
    }

    public int getTopNegativeIconConstants() {
        return this.topNegativeIconConstants;
    }

    public int getTopNegativeStringId() {
        return this.topNegativeStringId;
    }

    public int[] getBottomPositiveBtnBgColorIds() {
        return this.bottomPositiveBtnBgColorIds;
    }

    public int getBottomPositiveIconConstants() {
        return this.bottomPositiveIconConstants;
    }

    public int getBottomPositiveStringId() {
        return this.bottomPositiveStringId;
    }

    public int[] getBottomNegativeBtnBgColorIds() {
        return this.bottomNegativeBtnBgColorIds;
    }

    public int getBottomNegativeIconConstants() {
        return this.bottomNegativeIconConstants;
    }

    public int getBottomNegativeStringId() {
        return this.bottomNegativeStringId;
    }

    public OnClickListener getListener() {
        return this.listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public boolean isForbidSwipeToDismiss() {
        return this.forbidSwipeToDismiss;
    }

    public void setForbidSwipeToDismiss(boolean forbidSwipeToDismiss) {
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
    }

    @Override
    public String toString() {
        return "ThreeIconBtnBean{context=" + this.context + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss + ", listener=" + this.listener + '}';
    }

    public FourIconBtnDialog create() {
        FourIconBtnDialog dialog = new FourIconBtnDialog(this.context, true);
        dialog.initData(this);
        return dialog;
    }
}