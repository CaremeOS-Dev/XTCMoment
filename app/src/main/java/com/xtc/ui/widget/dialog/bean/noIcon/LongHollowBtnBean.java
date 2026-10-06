package com.xtc.ui.widget.dialog.bean.noIcon;

import android.content.Context;

/** Data for the single full-width hollow-button dialog. */
public class LongHollowBtnBean {
    private int btnStringId;
    private Context context;
    private boolean forbidSwipeToDismiss;
    private CharSequence titleString;

    public LongHollowBtnBean(Context context, boolean forbidSwipeToDismiss, CharSequence titleString, int btnStringId) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.titleString = titleString;
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

    @Override
    public String toString() {
        return "LongHollowBtnBean{context=" + this.context + ", forbidSwipeToDismiss=" + this.forbidSwipeToDismiss + ", titleString=" + ((Object) this.titleString) + ", btnStringId=" + this.btnStringId + '}';
    }
}
