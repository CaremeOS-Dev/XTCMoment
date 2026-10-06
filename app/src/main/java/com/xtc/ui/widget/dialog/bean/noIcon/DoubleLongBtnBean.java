package com.xtc.ui.widget.dialog.bean.noIcon;

import android.content.Context;

/** Data for the dialog with two stacked full-width buttons. */
public class DoubleLongBtnBean {
    private String btnBottomString;
    private String btnTopString;
    private Context context;
    private boolean forbidSwipeToDismiss;
    private CharSequence titleString;

    public DoubleLongBtnBean(Context context, boolean forbidSwipeToDismiss, CharSequence titleString, String btnTopString, String btnBottomString) {
        this.context = context;
        this.forbidSwipeToDismiss = forbidSwipeToDismiss;
        this.titleString = titleString;
        this.btnTopString = btnTopString;
        this.btnBottomString = btnBottomString;
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

    public String getBtnTopString() {
        return this.btnTopString;
    }

    public void setBtnTopString(String btnTopString) {
        this.btnTopString = btnTopString;
    }

    public String getBtnBottomString() {
        return this.btnBottomString;
    }

    public void setBtnBottomString(String btnBottomString) {
        this.btnBottomString = btnBottomString;
    }
}
