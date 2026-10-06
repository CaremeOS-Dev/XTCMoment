package com.xtc.moment.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.xtc.utils.system.model.I18n;

import java.util.Locale;

/**
 * 多语言判定工具。
 */
public class LanguageUtils {

    private static final String TAG = "Launcher_LanguageSourceUtils";

    public static String getLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale.getLanguage();
    }

    public static boolean isEn(Context context) {
        return !TextUtils.isEmpty(getLanguage(context))
                && getLanguage(context).equals(new Locale(I18n.Language.ENGLISH).getLanguage());
    }

    public static boolean isIn_rID(Context context) {
        return !TextUtils.isEmpty(getLanguage(context))
                && getLanguage(context).equals(new Locale(I18n.Language.INDONESIAN).getLanguage());
    }

    public static boolean isTh(Context context) {
        return !TextUtils.isEmpty(getLanguage(context))
                && getLanguage(context).equals(new Locale(I18n.Language.THAI).getLanguage());
    }

    public static boolean isZhHK(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale != null && locale.equals(new Locale(I18n.Language.CHINESE, I18n.Region.HONG_KONG));
    }

    public static boolean isZhCN(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale != null && locale.equals(new Locale(I18n.Language.CHINESE, I18n.Region.CHINA));
    }
}