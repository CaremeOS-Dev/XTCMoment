package com.xtc.utils.system;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

/** Version comparison / normalisation helpers. */
public class VersionUtil {

    private VersionUtil() {
    }

    /** Compares two dotted version strings segment by segment. */
    public static int compareVersion(String first, String second) {
        String[] firstParts = first.split("\\.");
        String[] secondParts = second.split("\\.");
        int index = 0;
        while (index < firstParts.length) {
            int nextIndex = index + 1;
            if (firstParts.length == nextIndex) {
                return Integer.valueOf(firstParts[index]).compareTo(Integer.valueOf(secondParts[index]));
            }
            int result = Integer.valueOf(firstParts[index]).compareTo(Integer.valueOf(secondParts[index]));
            if (result != 0) {
                return result;
            }
            index = nextIndex;
        }
        return 0;
    }

    /** Normalises a version to {@code major.minor.patch}, defaulting to 1.0.0. */
    public static String normalize(String version) {
        if (TextUtils.isEmpty(version)
                || !(version.matches("[1-9]\\d*.[0-9]\\d*.[0-9]\\d*")
                || version.matches("[1-9]\\d*.[0-9]\\d*.[0-9]\\d*..*"))) {
            return "1.0.0";
        }
        String[] parts = version.split("\\.");
        return parts[0] + "." + parts[1] + "." + parts[2];
    }

    /** Normalised version name of this package. */
    public static String getVersionName(Context context) {
        return normalize(getRawVersionName(context));
    }

    /** Raw {@code versionName} of this package. */
    public static String getRawVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /** {@code versionCode} of this package. */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 16384);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}