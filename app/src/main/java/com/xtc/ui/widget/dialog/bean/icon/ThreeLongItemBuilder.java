package com.xtc.ui.widget.dialog.bean.icon;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import com.xtc.ui.widget.dialog.ThreeLongItemDialog;
import com.xtc.ui.widget.dialog.bean.BaseDialogBean;

/** 三个长条目对话框的构建参数。 */
public class ThreeLongItemBuilder extends BaseDialogBean {
    private int[] bottomBtnBgColorIds;
    private int bottomCancelStringId;
    private int bottomIconConstants;
    private int bottomStringColorInt;
    private int bottomStringId;
    private Context context;
    private boolean forbidSwipeToDismiss;
    private OnClickListener listener;
    private int[] middleBtnBgColorIds;
    private int middleIconConstants;
    private int middleStringColorInt;
    private int middleStringId;
    private int[] topBtnBgColorIds;
    private int topIconConstants;
    private int topStringColorInt;
    private int topStringId;

    /** 条目点击回调。 */
    public interface OnClickListener {
        void onBottomItemClick(Dialog dialog, View view);

        void onMiddleItemClick(Dialog dialog, View view);

        void onTopItemClick(Dialog dialog, View view);

        void oncancelClick(Dialog dialog, View view);
    }

    public ThreeLongItemBuilder(Context context, OnClickListener listener) {
        this(context, true, listener);
    }

    public ThreeLongItemBuilder(Context context, boolean forbidSwipeToDismiss, OnClickListener listener) {
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

    public ThreeLongItemBuilder setTopBtn(int[] bgColorIds, int iconConstant, int stringId) {
        setTopBtn(bgColorIds, iconConstant, stringId, -1);
        return this;
    }

    public ThreeLongItemBuilder setTopBtn(int[] bgColorIds, int iconConstant, int stringId, int stringColorInt) {
        this.topBtnBgColorIds = bgColorIds;
        this.topIconConstants = iconConstant;
        this.topStringId = stringId;
        this.topStringColorInt = stringColorInt;
        return this;
    }

    public ThreeLongItemBuilder setMiddleBtn(int[] bgColorIds, int iconConstant, int stringId) {
        setMiddleBtn(bgColorIds, iconConstant, stringId, -1);
        return this;
    }

    public ThreeLongItemBuilder setMiddleBtn(int[] bgColorIds, int iconConstant, int stringId, int stringColorInt) {
        this.middleBtnBgColorIds = bgColorIds;
        this.middleIconConstants = iconConstant;
        this.middleStringId = stringId;
        this.middleStringColorInt = stringColorInt;
        return this;
    }

    public ThreeLongItemBuilder setBottomBtn(int[] bgColorIds, int iconConstant, int stringId) {
        setBottomBtn(bgColorIds, iconConstant, stringId, -1);
        return this;
    }

    public ThreeLongItemBuilder setBottomBtn(int[] bgColorIds, int iconConstant, int stringId, int stringColorInt) {
        this.bottomBtnBgColorIds = bgColorIds;
        this.bottomIconConstants = iconConstant;
        this.bottomStringId = stringId;
        this.bottomStringColorInt = stringColorInt;
        return this;
    }

    public ThreeLongItemBuilder setCancelBtn(int cancelStringId) {
        this.bottomCancelStringId = cancelStringId;
        return this;
    }

    public int[] getTopBtnBgColorIds() {
        return this.topBtnBgColorIds;
    }

    public int getTopIconConstants() {
        return this.topIconConstants;
    }

    public int getTopStringId() {
        return this.topStringId;
    }

    public int[] getMiddleBtnBgColorIds() {
        return this.middleBtnBgColorIds;
    }

    public int getMiddleIconConstants() {
        return this.middleIconConstants;
    }

    public int getMiddleStringId() {
        return this.middleStringId;
    }

    public int[] getBottomBtnBgColorIds() {
        return this.bottomBtnBgColorIds;
    }

    public int getBottomIconConstants() {
        return this.bottomIconConstants;
    }

    public int getBottomStringId() {
        return this.bottomStringId;
    }

    public int getTopStringColorInt() {
        return this.topStringColorInt;
    }

    public int getMiddleStringColorInt() {
        return this.middleStringColorInt;
    }

    public int getBottomStringColorInt() {
        return this.bottomStringColorInt;
    }

    public int getBottomCancelStringId() {
        return this.bottomCancelStringId;
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

    public ThreeLongItemDialog create() {
        ThreeLongItemDialog dialog = new ThreeLongItemDialog(this.context, true);
        dialog.initData(this);
        return dialog;
    }
}