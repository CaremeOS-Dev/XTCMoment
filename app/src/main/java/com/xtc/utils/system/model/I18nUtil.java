package com.xtc.utils.system.model;

import com.xtc.utils_screenshot_carry_data.ScreenshotUtils;

/** Joins a language and region code with the standard separator. */
public class I18nUtil {

    private I18nUtil() {
    }

    public static String join(String language, String region) {
        return language + ScreenshotUtils.SEPARATOR + region;
    }
}