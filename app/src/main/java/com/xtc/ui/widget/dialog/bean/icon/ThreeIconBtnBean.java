package com.xtc.ui.widget.dialog.bean.icon;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.xtc.ui.widget.dialog.bean.BaseDialogBean;

import java.util.Arrays;

/** Data for a three-icon-button dialog (two side by side, one full width). */
public class ThreeIconBtnBean extends BaseDialogBean {

    private int bottomStringId;
    private Context context;
    private boolean forbidSwipeToDismiss;
    private int[] leftBtnBgColorIds;
    private int leftIconConstants;
    private int leftStringId;
    private OnClickListener listener;
    private int[] rightBtnBgColorIds;
    private int rightIconConstants;
    private int rightStringId;

    /** Callbacks for the three buttons. */
    public interface OnClickListener extends NormalIconDialogBean.OnClickListener {
        @Override
        void onBottomBtnClick(Dialog dialog, View view);

        @Override
        void onLeftBtnClick(Dialog dialog, View view);

        @Override
        void onRightBtnClick(Dialog dialog, View view);
    }

    public ThreeIconBtnBean(Context context, boolean forbidSwipeToDismiss, int[] leftBtnBgColorIds, int leftIconConstants,
                            int leftStringId, int[] rightBtnBgColorIds, int rightIconConstants, int rightStringId,
                            int bottomStringId, OnClickListener listener) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.leftBtnBgColorIds = leftBtnBgColorIds;
        this.leftIconConstants = leftIconConstants;
        this.leftStringId = leftStringId;
        this.rightBtnBgColorIds = rightBtnBgColorIds;
        this.rightIconConstants = rightIconConstants;
        this.rightStringId = rightStringId;
        this.bottomStringId = bottomStringId;
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

    public int getBottomStringId() {
        return this.bottomStringId;
    }

    public void setBottomStringId(int bottomStringId) {
        this.bottomStringId = bottomStringId;
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
        return "ThreeIconBtnBean{context=" + this.context + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss + ", leftBtnBgColorIds=" + Arrays.toString(this.leftBtnBgColorIds) + ", leftIconConstants=" + this.leftIconConstants + ", leftStringId=" + this.leftStringId + ", rightBtnBgColorIds=" + Arrays.toString(this.rightBtnBgColorIds) + ", rightIconConstants=" + this.rightIconConstants + ", rightStringId=" + this.rightStringId + ", bottomStringId=" + this.bottomStringId + ", listener=" + this.listener + '}';
    }
}
