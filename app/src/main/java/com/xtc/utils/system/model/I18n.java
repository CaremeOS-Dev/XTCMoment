package com.xtc.utils.system.model;

/** Language and region codes used across the system apps. */
public interface I18n {

    /** ISO language codes supported by the watch firmware. */
    interface Language {
        String CHINESE = "zh";
        String ENGLISH = "en";
        String INDONESIAN = "in";
        String THAI = "th";
        String MALAY = "ms";
    }

    /** Region codes reported by the firmware. */
    interface Region {
        String CHINA = "CN";
        String HONG_KONG = "HK";
        String TAIWAN = "TW";
        String INDONESIA = "ID";
        String THAILAND = "TH";
        String UNITED_STATES = "US";
        String MALAYSIA = "MY";
        String UNKNOWN = "";
    }
}