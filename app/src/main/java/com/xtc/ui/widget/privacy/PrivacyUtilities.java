package com.xtc.ui.widget.privacy;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.system.model.I18n;
import com.xtc.utils.system.model.Platform;

import java.util.Locale;

/**
 * Builds the query string appended to privacy/agreement URLs so the H5 page can
 * pick the right locale and watch model.
 */
public class PrivacyUtilities {

    private static final String TAG = "PrivacyUtilities";
    private static final String AMPERSAND = "&";
    private static final String QUESTION_MARK = "?";
    private static final String APP_BRAND = "appBrand=";
    private static final String LANGUAGE = "language=";
    private static final String PACKAGE_NAME = "packageName=";
    private static final String W_MODEL = "wModel=";
    private static final String SUFFIX = "#/";
    private static final String TCX = "tiancaixing";
    private static final String XTC = "okii";

    /** Model codes for the inner-model field. */
    interface Model {
        interface Inner {
            String Unknown = "";
            String I11 = "IB";
            String I13 = "I13";
            String IDI13 = "IDI13";
        }
    }

    /** Region codes for the locale-region field. */
    interface Region {
        String Unknown = "";
        String Indonesia = "ID";
    }

    private static String watchInnerModel;

    static String getLocalLanguage(Context context) {
        String localLanguage = getLocalLanguageImpl(context);
        return localLanguage.startsWith(I18n.Language.THAI) ? I18n.Language.THAI : localLanguage;
    }

    static String getWatchInnerModel() {
        if (!TextUtils.isEmpty(watchInnerModel)) {
            return watchInnerModel;
        }
        String localeRegion = getSystemLocaleRegion();
        String model;
        if (localeRegion.equals("") || isZhanxun()) {
            model = getServerInnerModel();
            if (model.equals("")) {
                model = getSystemInnerModel() + contactInnerModelExtend();
            }
        } else {
            if (getSystemInnerModel().equals(Model.Inner.I13) && localeRegion.equals(Region.Indonesia)) {
                return Model.Inner.IDI13;
            }
            model = getSystemInnerModel() + "-" + localeRegion + contactInnerModelExtend();
        }
        watchInnerModel = model;
        return watchInnerModel;
    }

    private static String getLocalLanguageImpl(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources == null ? null : resources.getConfiguration();
        Locale locale = configuration == null ? null : configuration.locale;
        if (locale == null) {
            locale = Locale.getDefault();
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (TextUtils.isEmpty(country)) {
            return language;
        }
        return language + "-" + country;
    }

    private static String getSystemLocaleRegion() {
        return getString("ro.product.locale.region", "");
    }

    private static boolean isZhanxun() {
        return !isGaotong();
    }

    private static boolean isGaotong() {
        return Platform.QCOM.equals(getString("ro.hardware", ""));
    }

    private static String getServerInnerModel() {
        return getString("persist.sys.serverinner", "");
    }

    private static String getSystemInnerModel() {
        return getString("ro.product.innermodel", Model.Inner.I11);
    }

    private static String contactInnerModelExtend() {
        String ext = getString("ro.product.innermodel.ex", "");
        if (TextUtils.isEmpty(ext)) {
            return "";
        }
        return "-" + ext;
    }

    private static String getString(String key, String defaultValue) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, key, defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String jointUrl(String url, Context context, String packageName) {
        return jointUrl(url, context, packageName, 0);
    }

    public static String jointUrl(String url, Context context, String packageName, int type) {
        LogUtil.d(TAG, " originalUrl：   " + url);
        if (TextUtils.isEmpty(url)) {
            LogUtil.w(TAG, " OriginalUrl is Empty");
            return null;
        }
        String separator = QUESTION_MARK;
        boolean hasQuery = url.contains(QUESTION_MARK);
        boolean isOkii = url.contains(XTC);
        if (url.contains(TCX) || isOkii) {
            if (hasQuery) {
                separator = AMPERSAND;
            }
            if (TextUtils.isEmpty(packageName)) {
                packageName = context.getPackageName();
            }
            url = url + (separator + W_MODEL + getWatchInnerModel() + AMPERSAND + PACKAGE_NAME + packageName + AMPERSAND + LANGUAGE + getLocalLanguage(context) + AMPERSAND + APP_BRAND + Build.BRAND + getUrlTypeString(type) + SUFFIX);
        }
        LogUtil.d(TAG, " final url：   " + url);
        return url;
    }

    private static String getUrlTypeString(int type) {
        if (type == 0) {
            return "";
        }
        return "&type=" + type;
    }
}
