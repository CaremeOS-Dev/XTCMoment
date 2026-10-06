package com.xtc.bigdata.collector.utils;

import android.text.TextUtils;

/** Encodes the "uses mobile traffic" judgement flag. */
public class Judgment {

    private static final String SYMBOL = "|";
    public static final int TYPE_MOBILE_TRAFFIC = 1;

    private Judgment() {
    }

    public static boolean isMobileTraffic(String judgment) {
        return isExistType(judgment, TYPE_MOBILE_TRAFFIC);
    }

    public static String useMobileTraffic(String judgment, boolean useMobileTraffic) {
        if (useMobileTraffic) {
            return addJudgment(judgment, TYPE_MOBILE_TRAFFIC);
        }
        return removeJudgment(judgment, TYPE_MOBILE_TRAFFIC);
    }

    public static boolean isExistType(String judgment, int type) {
        return !TextUtils.isEmpty(judgment) && judgment.contains(format(type));
    }

    public static String addJudgment(String judgment, int type) {
        String token = format(type);
        if (TextUtils.isEmpty(judgment)) {
            return token;
        }
        if (judgment.contains(token)) {
            return judgment;
        }
        return judgment + token;
    }

    public static String removeJudgment(String judgment, int type) {
        String token = format(type);
        return (TextUtils.isEmpty(judgment) || !judgment.contains(token)) ? judgment : judgment.replace(token, "");
    }

    private static String format(int type) {
        return SYMBOL + type + SYMBOL;
    }
}