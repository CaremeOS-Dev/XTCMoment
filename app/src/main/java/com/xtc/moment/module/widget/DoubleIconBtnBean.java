package com.xtc.moment.module.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.xtc.ui.widget.dialog.bean.BaseDialogBean;
import com.xtc.ui.widget.dialog.bean.icon.NormalIconDialogBean;

import java.util.Arrays;

/**
 * 双图标按钮弹窗参数。
 */
public class DoubleIconBtnBean extends BaseDialogBean {

    private Context context;
    private boolean forbidSwipeToDismiss;
    private int[] leftBtnBgColorIds;
    private int leftIconConstants;
    private int leftStringId;
    private int[] rightBtnBgColorIds;
    private int rightIconConstants;
    private int rightStringId;
    private boolean centerInVertical;
    private OnClickListener listener;

    public interface OnClickListener extends NormalIconDialogBean.OnClickListener {
        @Override
        void onLeftBtnClick(Dialog dialog, View view);

        @Override
        void onRightBtnClick(Dialog dialog, View view);
    }

    public DoubleIconBtnBean(Context context, boolean forbidSwipeToDismiss, int[] leftBtnBgColorIds,
            int leftIconConstants, int leftStringId, int[] rightBtnBgColorIds, int rightIconConstants,
            int rightStringId, boolean centerInVertical, OnClickListener listener) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.leftBtnBgColorIds = leftBtnBgColorIds;
        this.leftIconConstants = leftIconConstants;
        this.leftStringId = leftStringId;
        this.rightBtnBgColorIds = rightBtnBgColorIds;
        this.rightIconConstants = rightIconConstants;
        this.rightStringId = rightStringId;
        this.centerInVertical = centerInVertical;
        this.listener = listener;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int[] getLeftBtnBgColorIds() {
        return this.leftBtnBgColorIds;
    }

    public void setLeftBtnBgColorIds(int[] leftBtnBgColorIds) {
        this.leftBtnBgColorIds = leftBtnBgColorIds;
    }

    public int getLeftIconConstants() {
        return this.leftIconConstants;
    }

    public void setLeftIconConstants(int leftIconConstants) {
        this.leftIconConstants = leftIconConstants;
    }

    public int getLeftStringId() {
        return this.leftStringId;
    }

    public void setLeftStringId(int leftStringId) {
        this.leftStringId = leftStringId;
    }

    public int[] getRightBtnBgColorIds() {
        return this.rightBtnBgColorIds;
    }

    public void setRightBtnBgColorIds(int[] rightBtnBgColorIds) {
        this.rightBtnBgColorIds = rightBtnBgColorIds;
    }

    public int getRightIconConstants() {
        return this.rightIconConstants;
    }

    public void setRightIconConstants(int rightIconConstants) {
        this.rightIconConstants = rightIconConstants;
    }

    public int getRightStringId() {
        return this.rightStringId;
    }

    public void setRightStringId(int rightStringId) {
        this.rightStringId = rightStringId;
    }

    public boolean isCenterInVertical() {
        return this.centerInVertical;
    }

    public void setCenterInVertical(boolean centerInVertical) {
        this.centerInVertical = centerInVertical;
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
        return "DoubleIconBtnBean{context=" + this.context + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss
                + ", leftBtnBgColorIds=" + Arrays.toString(this.leftBtnBgColorIds) + ", leftIconConstants="
                + this.leftIconConstants + ", leftStringId=" + this.leftStringId + ", rightBtnBgColorIds="
                + Arrays.toString(this.rightBtnBgColorIds) + ", rightIconConstants=" + this.rightIconConstants
                + ", rightStringId=" + this.rightStringId + ", centerInVertical=" + this.centerInVertical
                + ", listener=" + this.listener + '}';
    }
}