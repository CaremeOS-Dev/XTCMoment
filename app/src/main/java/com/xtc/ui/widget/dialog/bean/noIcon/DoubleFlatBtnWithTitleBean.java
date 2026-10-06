package com.xtc.ui.widget.dialog.bean.noIcon;

import android.content.Context;

/** Data for the two-button flat dialog that also shows a title and content. */
public class DoubleFlatBtnWithTitleBean {
    private CharSequence contentString;
    private Context context;
    private CharSequence extraString;
    private boolean forbidSwipeToDismiss;
    private int leftStringId;
    private int rightStringId;
    private CharSequence titleString;

    public DoubleFlatBtnWithTitleBean(Context context, boolean forbidSwipeToDismiss, CharSequence titleString, CharSequence contentString, int leftStringId, int rightStringId) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.titleString = titleString;
        this.contentString = contentString;
        this.leftStringId = leftStringId;
        this.rightStringId = rightStringId;
    }

    public DoubleFlatBtnWithTitleBean(Context context, boolean forbidSwipeToDismiss, CharSequence titleString, CharSequence contentString, CharSequence extraString, int leftStringId, int rightStringId) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.titleString = titleString;
        this.contentString = contentString;
        this.extraString = extraString;
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

    public CharSequence getContentString() {
        return this.contentString;
    }

    public void setContentString(CharSequence contentString) {
        this.contentString = contentString;
    }

    public CharSequence getExtraString() {
        return this.extraString;
    }

    public void setExtraString(CharSequence extraString) {
        this.extraString = extraString;
    }

    @Override
    public String toString() {
        return "DoubleFlatBtnWithTitleBean{context=" + this.context + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss + ", titleString=" + ((Object) this.titleString) + ", contentString=" + ((Object) this.contentString) + ", extraString=" + ((Object) this.extraString) + ", leftStringId=" + this.leftStringId + ", rightStringId=" + this.rightStringId + '}';
    }
}
