package com.xtc.ui.widget.head;

import android.content.Context;

import com.xtc.moment.module.Constants;
import com.xtc.moment.R;

/** Resolves the default avatar resource of the watch account. */
public class DefaultHeadManager {

    private DefaultHeadManager() {
    }

    /** @return the {@code android.resource} uri of the default avatar. */
    public static String getDefaultHead(Context context) {
        return "android.resource://" + context.getPackageName()
                + Constants.ProviderConstants.SEPARATOR_CONTENT_PROVIDER + R.drawable.default_head;
    }
}