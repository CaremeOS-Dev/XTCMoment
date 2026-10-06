package com.xtc.httplib.util;

import java.util.Locale;

/** Country checks for the South-Asia region. */
public class CountryUtil {

    private CountryUtil() {
    }

    /** @return true for TW / ID / MY / TH locales. */
    public static boolean isSaCountry() {
        String country = Locale.getDefault().getCountry();
        return "TW".equals(country) || "ID".equals(country) || "MY".equals(country) || "TH".equals(country);
    }
}