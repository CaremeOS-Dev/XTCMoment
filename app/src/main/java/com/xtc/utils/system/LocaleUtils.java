package com.xtc.utils.system;

import android.content.Context;
import android.os.Build;

import com.xtc.utils.system.model.I18n;

import java.util.Locale;

/** Locale inspection helpers. */
public class LocaleUtils {

    private LocaleUtils() {
    }

    /** Current locale, defaulting to English when unavailable. */
    public static Locale getLocale(Context context) {
        Locale locale;
        if (context == null) {
            return new Locale(I18n.Language.ENGLISH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale != null ? locale : new Locale(I18n.Language.ENGLISH);
    }

    /** Current language code. */
    public static String getLanguage(Context context) {
        return getLocale(context).getLanguage();
    }

    /** @return true when the UI language is Chinese. */
    public static boolean isChinese(Context context) {
        return getLanguage(context).equals(new Locale(I18n.Language.CHINESE).getLanguage());
    }

    /** @return true for traditional Chinese (Hong Kong). */
    public static boolean isTraditionalChinese(Context context) {
        return getLocale(context).equals(new Locale(I18n.Language.CHINESE, "HK"));
    }

    /** @return true when the UI language is English. */
    public static boolean isEnglish(Context context) {
        return getLanguage(context).equals(new Locale(I18n.Language.ENGLISH).getLanguage());
    }

    /** @return true when the UI language is Indonesian. */
    public static boolean isIndonesian(Context context) {
        return getLanguage(context).equals(new Locale(I18n.Language.INDONESIAN).getLanguage());
    }

    /** @return true when the UI language is Thai. */
    public static boolean isThai(Context context) {
        return getLanguage(context).equals(new Locale(I18n.Language.THAI).getLanguage());
    }

    /** @return true when the UI language is Malay. */
    public static boolean isMalay(Context context) {
        return getLanguage(context).equals(new Locale(I18n.Language.MALAY).getLanguage());
    }

    private static boolean isGerman(Context context) {
        return getLanguage(context).equals(new Locale("de").getLanguage());
    }

    private static boolean isSpanish(Context context) {
        return getLanguage(context).equals(new Locale("es").getLanguage());
    }
}