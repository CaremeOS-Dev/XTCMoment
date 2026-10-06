package com.xtc.ui.widget.dialog.bean.icon;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import java.util.Arrays;

/** Data for {@code NormalIconDialog}: one/two/three icon buttons plus title. */
public class NormalIconDialogBean {

    private int style;
    private boolean forbidSwipeToDismiss;
    private Context context;
    private CharSequence titleString;
    private int[] leftBtnBgColorIds;
    private int leftIconConstants;
    private int leftStringId;
    private int[] rightBtnBgColorIds;
    private int rightIconConstants;
    private int rightStringId;
    private int bottomStringId;
    private OnClickListener listener;

    /** Callbacks for the up-to-three buttons. */
    public interface OnClickListener {
        void onBottomBtnClick(Dialog dialog, View view);

        void onLeftBtnClick(Dialog dialog, View view);

        void onRightBtnClick(Dialog dialog, View view);
    }

    public NormalIconDialogBean(int style, boolean forbidSwipeToDismiss, Context context, CharSequence titleString,
                                int[] leftBtnBgColorIds, int leftIconConstants, int leftStringId,
                                int[] rightBtnBgColorIds, int rightIconConstants, int rightStringId,
                                int bottomStringId, OnClickListener listener) {
        this.style = style;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.context = context;
        this.titleString = titleString;
        this.leftBtnBgColorIds = leftBtnBgColorIds;
        this.leftIconConstants = leftIconConstants;
        this.leftStringId = leftStringId;
        this.rightBtnBgColorIds = rightBtnBgColorIds;
        this.rightIconConstants = rightIconConstants;
        this.rightStringId = rightStringId;
        this.bottomStringId = bottomStringId;
        this.listener = listener;
    }

    public int getStyle() {
        return this.style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public boolean isForbidSwipeToDismiss() {
        return this.forbidSwipeToDismiss;
    }

    public void setForbidSwipeToDismiss(boolean forbidSwipeToDismiss) {
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public CharSequence getTitleString() {
        return this.titleString;
    }

    public void setTitleString(CharSequence titleString) {
        this.titleString = titleString;
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

    @Override
    public String toString() {
        return "NormalIconDialogBean{style=" + this.style + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss + ", context=" + this.context + ", titleString=" + ((Object) this.titleString) + ", leftBtnBgColorIds=" + Arrays.toString(this.leftBtnBgColorIds) + ", leftIconConstants=" + this.leftIconConstants + ", leftStringId=" + this.leftStringId + ", rightBtnBgColorIds=" + Arrays.toString(this.rightBtnBgColorIds) + ", rightIconConstants=" + this.rightIconConstants + ", rightStringId=" + this.rightStringId + ", bottomStringId=" + this.bottomStringId + ", listener=" + this.listener + '}';
    }
}
