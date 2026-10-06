package com.xtc.utils.system;

/** Build-flavour checks driven by the {@code ro.build.version.auth} property. */
public class CtaUtils {

    private static final int UNUSED = 1;
    private static final String BUILD_AUTH = SystemPropertyUtil.get(SystemProperty.BUILD_VERSION_AUTH, "");
    private static boolean ctaPermission = SystemPropertyUtil.getBoolean(SystemProperty.CTA_PERMISSION, false);

    private CtaUtils() {
    }

    /** @return true for the CTA build. */
    public static boolean isCta() {
        return "cta".equals(BUILD_AUTH);
    }

    /** @return true for the China Mobile build. */
    public static boolean isCmcc() {
        return "cmcc".equals(BUILD_AUTH);
    }

    /** @return true for the China Unicom build. */
    public static boolean isCu() {
        return "cu".equals(BUILD_AUTH);
    }

    /** @return true for the China Telecom build. */
    public static boolean isCt() {
        return "ct".equals(BUILD_AUTH);
    }

    /** @return true when the CTA permission property is enabled. */
    public static boolean hasCtaPermission() {
        if (!ctaPermission) {
            ctaPermission = SystemPropertyUtil.getBoolean(SystemProperty.CTA_PERMISSION, false);
        }
        return ctaPermission;
    }

    public static void setCtaPermission(boolean enabled) {
        ctaPermission = enabled;
        SystemPropertyUtil.setBoolean(SystemProperty.CTA_PERMISSION, enabled);
    }

    /** @return true when the CTA version property equals 1. */
    public static boolean isCtaVersionOne() {
        return SystemPropertyUtil.getInt(SystemProperty.CTA_VERSION, 0) == 1;
    }
}