package com.xtc.ui.widget.dialog.bean.noIcon;

import android.content.Context;

/** Data for the two-button flat dialog. */
public class DoubleFlatBtnBean {
    private Context context;
    private boolean forbidSwipeToDismiss;
    private int leftStringId;
    private int rightStringId;
    private CharSequence titleString;

    public DoubleFlatBtnBean(Context context, boolean forbidSwipeToDismiss, CharSequence titleString, int leftStringId, int rightStringId) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.titleString = titleString;
        this.leftStringId = leftStringId;
        this.rightStringId = rightStringId;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isForbidSwipeToDismiss() {
        return this.forbidSwipeToDismiss;
    }

    public void setForbidSwipeToDismiss(boolean forbidSwipeToDismiss) {
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
    }

    public CharSequence getTitleString() {
        return this.titleString;
    }

    public void setTitleString(CharSequence titleString) {
        this.titleString = titleString;
    }

    public int getLeftStringId() {
        return this.leftStringId;
    }

    public void setLeftStringId(int leftStringId) {
        this.leftStringId = leftStringId;
    }

    public int getRightStringId() {
        return this.rightStringId;
    }

    public void setRightStringId(int rightStringId) {
        this.rightStringId = rightStringId;
    }

    @Override
    public String toString() {
        return "DoubleFlatBtnBean{context=" + this.context + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss + ", titleString=" + ((Object) this.titleString) + ", leftStringId=" + this.leftStringId + ", rightStringId=" + this.rightStringId + '}';
    }
}
