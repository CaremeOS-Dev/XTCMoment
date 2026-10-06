package com.xtc.ui.widget.permission.cta.interfaces;

/** Result callback for the CTA (consent) permission dialog. */
public interface ICtaPermissionCallback {
    void allow();

    void refuse();
}
