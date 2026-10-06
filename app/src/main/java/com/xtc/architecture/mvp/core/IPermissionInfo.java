package com.xtc.architecture.mvp.core;

/** Describes the CTA copy and reminder state for a permission prompt. */
public interface IPermissionInfo {
    String getCtaTitle();

    String getTip();

    boolean needCheckPermissionReminder();

    void setCheckPermissionReminder();
}
