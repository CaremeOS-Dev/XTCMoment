package com.xtc.utils.system;

import android.os.Build;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.system.model.I18n;
import com.xtc.utils.system.model.I18nUtil;
import com.xtc.utils.system.model.Platform;
import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

import java.util.Locale;

/** Reads the watch model / region / hardware information from system properties. */
public class WatchModelUtil {

    private static final String TAG = "WatchModelUtil";
    /** Separator used between model and region segments. */
    private static final String SEPARATOR = "-";

    private static String outerModel = "";
    private static String primaryModel = "";
    private static String buildType = "";
    private static String careMeVersion;
    private static Boolean careMeAvailable;
    private static String watchInnerModel;

    /** Writes the {@code persist.sys.serverinner} property. */
    public static void setServerInnerModel(String value) {
        SystemPropertyUtil.set(SystemProperty.SERVER_INNER, value);
    }

    /** Full inner-model string including the region suffix. */
    public static String getWatchInnerModel() {
        if (!TextUtils.isEmpty(watchInnerModel)) {
            return watchInnerModel;
        }
        String region = getLocaleRegion();
        String model;
        if (region.equals("") || isZhanxunPlatform()) {
            model = getServerInnerModelProperty();
            if (model.equals("")) {
                model = getInnerModel() + getInnerModelExt();
            }
        } else {
            if (getInnerModel().equals("I13") && region.equals("ID")) {
                return "IDI13";
            }
            model = getInnerModel() + SEPARATOR + region + getInnerModelExt();
        }
        watchInnerModel = model;
        return watchInnerModel;
    }

    /** The inner model reported by {@code ro.product.innermodel} (default IB). */
    static String getInnerModel() {
        return SystemPropertyUtil.get(SystemProperty.PRODUCT_INNER_MODEL, "IB");
    }

    private static String getServerInnerModelProperty() {
        return SystemPropertyUtil.get(SystemProperty.SERVER_INNER, "");
    }

    private static String getProductLocale() {
        return SystemPropertyUtil.get(SystemProperty.LOCALE, "");
    }

    private static String getLocaleRegion() {
        return SystemPropertyUtil.get(SystemProperty.LOCALE_REGION, "");
    }

    /** Current UI language code. */
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /** Current region code (special-cased for region-changable models). */
    public static String getRegion() {
        if (isGaotong()) {
            return getRegionForGaotong();
        }
        return getLocaleRegion();
    }

    /** Optional inner-model extension from {@code ro.product.innermodel.ex}. */
    private static String getInnerModelExt() {
        String ext = SystemPropertyUtil.get(SystemProperty.PRODUCT_INNER_MODEL_EX, "");
        if (TextUtils.isEmpty(ext)) {
            return "";
        }
        return SEPARATOR + ext;
    }

    /** Region resolution for region-changable (Qualcomm) models. */
    private static String getRegionForGaotong() {
        String innerModel = getInnerModel();
        String productLocale = getProductLocale();
        if (isModelRegionChangable(innerModel)) {
            if (productLocale.equals(I18nUtil.join(I18n.Language.CHINESE, I18n.Region.CHINA))) {
                return I18n.Region.CHINA;
            }
            String localeRegion = getLocaleRegion();
            if (!localeRegion.equals("")) {
                return localeRegion;
            }
            String serverInner = getServerInnerModelProperty();
            if (serverInner.equals("")) {
                return "";
            }
            int length = serverInner.length();
            return serverInner.substring(length - 2, length);
        }
        if (productLocale.equals("")) {
            return "";
        }
        int length = productLocale.length();
        return productLocale.substring(length - 2, length);
    }

    /** @deprecated use {@link #isRegion(String)} with {@code TH}. */
    @Deprecated
    public static boolean isThailand() {
        return isRegion("TH");
    }

    /** @deprecated use {@link #isRegion(String)} with {@code ID}. */
    @Deprecated
    public static boolean isIndonesia() {
        return isRegion("ID");
    }

    /** @deprecated true when the watch is not a China variant. */
    @Deprecated
    public static boolean isOverseas() {
        return !(isRegion("CN") || isRegion(""));
    }

    /** Marketing model name ({@code ro.product.model}). */
    @Deprecated
    public static String getOuterModel() {
        if (TextUtils.isEmpty(outerModel)) {
            outerModel = SystemPropertyUtil.get(SystemProperty.PRODUCT_MODEL, SystemProperty.Model.Outer.XTC_Z3);
        }
        return outerModel;
    }

    /** Primary model name, preferring {@code ro.product.pri.model}. */
    public static String getPrimaryModel() {
        if (TextUtils.isEmpty(primaryModel)) {
            primaryModel = SystemPropertyUtil.get(SystemProperty.PRODUCT_PRIMARY_MODEL, "");
        }
        if (TextUtils.isEmpty(primaryModel)) {
            if (isModel(SystemProperty.Model.Inner.I12)) {
                primaryModel = getDefaultShowModel("XTC Z2y");
            } else if (isModel("IB")) {
                primaryModel = getDefaultShowModel("XTC Z3");
            } else {
                primaryModel = getOuterModel();
            }
        }
        return primaryModel;
    }

    private static String getDefaultShowModel(String fallback) {
        return SystemPropertyUtil.get("ro.product.showmodel", fallback);
    }

    /** @deprecated use {@link #isModel(String)} with {@code IB}. */
    @Deprecated
    public static boolean isIbModel() {
        return isModel("IB");
    }

    /** @deprecated use {@link #isModel(String)} with {@code I12}. */
    @Deprecated
    public static boolean isI12Model() {
        return isModel(SystemProperty.Model.Inner.I12);
    }

    /** @deprecated use {@link #isModel(String)} with {@code I13}. */
    @Deprecated
    public static boolean isI13Model() {
        return isModel("I13");
    }

    /** @deprecated use {@link #isModel(String)} with {@code I13C}. */
    @Deprecated
    public static boolean isI13CModel() {
        return isModel(SystemProperty.Model.Inner.I13C);
    }

    /** @deprecated use {@link #isModel(String)} with {@code I16}. */
    @Deprecated
    public static boolean isI16Model() {
        return isModel(SystemProperty.Model.Inner.I16);
    }

    /** @deprecated use {@link #isModel(String)} with {@code I17}. */
    @Deprecated
    public static boolean isI17Model() {
        return isModel(SystemProperty.Model.Inner.I17);
    }

    /** @deprecated use {@link #isModel(String)} with {@code I18}. */
    @Deprecated
    public static boolean isI18Model() {
        return isModel(SystemProperty.Model.Inner.I18);
    }

    /** Compares the inner model (before the region separator) with {@code model}. */
    public static boolean isInnerModel(String innerModel, String model) {
        if (innerModel == null || model == null) {
            LogUtil.e(TAG, "isModel error: innerModel == null || isModel == null");
            return false;
        }
        return model.equals(innerModel.split(SEPARATOR)[0]);
    }

    /** Compares the current inner model with {@code model}. */
    public static boolean isModel(String model) {
        if (model == null) {
            LogUtil.e(TAG, "isModel error: isModel");
            return false;
        }
        return isInnerModel(getInnerModel(), model);
    }

    /** Compares the current region with {@code region}. */
    public static boolean isRegion(String region) {
        if (region == null) {
            LogUtil.e(TAG, "isRegion error: isRegion");
            return false;
        }
        return getRegion().equals(region);
    }

    /** Compares the full inner-model string with {@code region}. */
    public static boolean isRegionOfInnerModel(String region) {
        if (region == null) {
            LogUtil.e(TAG, "checkRegionOfInnerModel error: isRegion == null");
            return false;
        }
        return isInnerModelRegion(getWatchInnerModel(), region);
    }

    /** Checks whether the full inner model ends with the given region. */
    public static boolean isInnerModelRegion(String innerModel, String region) {
        if (innerModel == null || region == null) {
            LogUtil.e(TAG, "checkRegionOfInnerModel error: innerModel == null || isRegion == null");
            return false;
        }
        if (region.equals("")) {
            return !innerModel.contains(SEPARATOR);
        }
        return innerModel.endsWith(region);
    }

    /** Checks whether the full inner model equals {@code model-region}. */
    public static boolean isInnerModelRegion(String innerModel, String model, String region) {
        if (innerModel == null || model == null || region == null) {
            LogUtil.e(TAG, "checkModelAndRegionOfInnerModel error: innerModel == null || isModel == null || isRegion == null");
            return false;
        }
        return innerModel.equals(model + SEPARATOR + region);
    }

    /** @return true on Spreadtrum (non region-changable) hardware. */
    public static boolean isZhanxunPlatform() {
        return !isGaotong();
    }

    /** @return true when the model is not region-changable. */
    public static boolean isNotRegionChangable() {
        return !isGaotong();
    }

    /** @return true on Qualcomm (region-changable) hardware. */
    public static boolean isGaotong() {
        return Platform.QCOM.equals(SystemPropertyUtil.get(SystemProperty.HARDWARE, ""));
    }

    /** @return true for the Y-series watch models. */
    public static boolean isWatchModelY(String innerModel) {
        if (innerModel != null) {
            return innerModel.equals(SystemProperty.Model.Inner.F2)
                    || innerModel.equals(SystemProperty.Model.Inner.IA)
                    || innerModel.equals(SystemProperty.Model.Inner.I2C)
                    || innerModel.equals(SystemProperty.Model.Inner.I3)
                    || innerModel.equals(SystemProperty.Model.Inner.I6)
                    || innerModel.equals(SystemProperty.Model.Inner.I8)
                    || innerModel.equals(SystemProperty.Model.Inner.Y01)
                    || innerModel.equals(SystemProperty.Model.Inner.Y02)
                    || innerModel.equals(SystemProperty.Model.Inner.Y1A)
                    || innerModel.equals(SystemProperty.Model.Inner.I25);
        }
        LogUtil.e(TAG, "isWatchModelY error: innerModel == null");
        return false;
    }

    /** @return true when the current model is region-changable. */
    static boolean isCurrentModelRegionChangable() {
        return isModelRegionChangable(getInnerModel());
    }

    /** @return true when {@code model} is region-changable (I13 or I18). */
    static boolean isModelRegionChangable(String model) {
        if (model == null) {
            LogUtil.e(TAG, "isModelRegionChangable error: model == null");
            return false;
        }
        String innerModel = model.split(SEPARATOR)[0];
        return innerModel.equals("I13") || innerModel.equals(SystemProperty.Model.Inner.I18);
    }

    /** @return true when the build is userdebug. */
    public static boolean isUserDebug() {
        if (TextUtils.isEmpty(buildType)) {
            buildType = Build.TYPE;
        }
        return "userdebug".equals(buildType);
    }

    /** {@code ro.product.careme.version} or an empty string. */
    public static String getCareMeVersion() {
        if (careMeVersion == null) {
            careMeVersion = SystemPropertyUtil.get(SystemProperty.CARE_ME_VERSION, "");
        }
        return careMeVersion;
    }

    /** @return true when the care-me version property is set. */
    public static boolean isCareMe() {
        if (careMeAvailable == null) {
            careMeAvailable = Boolean.valueOf(!TextUtils.isEmpty(getCareMeVersion()));
        }
        return careMeAvailable.booleanValue();
    }
}