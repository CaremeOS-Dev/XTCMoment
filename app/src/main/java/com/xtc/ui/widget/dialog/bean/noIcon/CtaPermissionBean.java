package com.xtc.ui.widget.dialog.bean.noIcon;

import java.util.List;

/** Data for the CTA (consent) permission dialog. */
public class CtaPermissionBean {
    private List<String> data;
    private boolean isAppStore;
    private String tip;
    private String title;

    public CtaPermissionBean(List<String> data, String title) {
        this(data, title, "");
    }

    public CtaPermissionBean(List<String> data, String title, String tip) {
        this(data, title, tip, false);
    }

    public CtaPermissionBean(List<String> data, String title, String tip, boolean isAppStore) {
        this.data = data;
        this.title = title;
        this.tip = tip;
        this.isAppStore = isAppStore;
    }

    public boolean isAppStore() {
        return this.isAppStore;
    }

    public void setAppStore(boolean appStore) {
        this.isAppStore = appStore;
    }

    public List<String> getData() {
        return this.data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTip() {
        return this.tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    @Override
    public String toString() {
        return "DoubleFlatBtnBean{, data=" + this.data + ", title=" + this.title + ", tip=" + this.tip + ", isAppStore=" + this.isAppStore + '}';
    }
}
