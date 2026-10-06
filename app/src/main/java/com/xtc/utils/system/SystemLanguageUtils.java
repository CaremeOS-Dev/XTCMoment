package com.xtc.utils.system;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;

import com.xtc.log.LogUtil;

import java.util.Locale;

/** Builds the {@code language-COUNTRY} string used by the network layer. */
public final class SystemLanguageUtils {

    private static final String TAG = "SystemLanguageUtils";
    private static final String SEPARATOR = "-";

    private SystemLanguageUtils() {
        throw new UnsupportedOperationException("You can't instantiate SystemLanguageUtils");
    }

    /** Current {@code language-COUNTRY}, or just the language when no country. */
    public static String getLocalLanguage(Context context) {
        Configuration configuration;
        Resources resources = context.getResources();
        Locale locale = (resources == null || (configuration = resources.getConfiguration()) == null)
                ? null : configuration.locale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        LogUtil.i(TAG, "getLocalLanguage language=" + language + ", country=" + country);
        if (TextUtils.isEmpty(country)) {
            return language;
        }
        return language + SEPARATOR + country;
    }
}