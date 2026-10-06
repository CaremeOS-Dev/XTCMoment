package com.xtc.ui.widget.privacy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Extras and URL-type constants shared by the privacy screens. */
public interface PrivacyCommon {

    interface PrivacyExtras {
        String EXTRA_CUSTOM_STRING = "custom_string";
        String EXTRA_PACKAGE_NAME = "package_name";
        String EXTRA_URL = "url";
        String EXTRA_URL_TYPE = "url_type";
    }

    /** Kinds of URL opened by {@code PrivacyUriActivity}. */
    @Retention(RetentionPolicy.SOURCE)
    @interface UrlType {
        int OTHER_TYPE = 0;
        int PRIVACY_TYPE = 1;
        int AGREEMENT_TYPE = 2;
    }
}
