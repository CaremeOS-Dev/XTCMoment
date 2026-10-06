package com.xtc.ui.widget.dialog.bean.noIcon;

import android.content.Context;

/** Data for the hollow-button dialog with a title and content. */
public class LongHollowBtnWithTitleBean {
    private int btnStringId;
    private CharSequence contentString;
    private Context context;
    private boolean forbidSwipeToDismiss;
    private CharSequence titleString;

    public LongHollowBtnWithTitleBean(Context context, boolean forbidSwipeToDismiss, CharSequence titleString, CharSequence contentString, int btnStringId) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.titleString = titleString;
        this.contentString = contentString;
        this.btnStringId = btnStringId;
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

    public int getBtnStringId() {
        return this.btnStringId;
    }

    public void setBtnStringId(int btnStringId) {
        this.btnStringId = btnStringId;
    }

    public CharSequence getContentString() {
        return this.contentString;
    }

    public void setContentString(CharSequence contentString) {
        this.contentString = contentString;
    }

    @Override
    public String toString() {
        return "LongHollowBtnWithTitleBean{context=" + this.context + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss + ", titleString=" + ((Object) this.titleString) + ", contentString=" + ((Object) this.contentString) + ", btnStringId=" + this.btnStringId + '}';
    }
}
