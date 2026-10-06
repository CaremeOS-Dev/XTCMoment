package com.xtc.moment.util;

import android.content.Context;
import android.text.TextUtils;

import com.xtc.log.LogUtil;
import com.xtc.utils.encode.MD5Util;

/** MD5 helpers used for the local photo names. */
public class MD5Utils {

    private static final String TAG = "MD5Utils";

    private MD5Utils() {
    }

    public static String getPhotoMd5(String localPath, String prefix) {
        if (TextUtils.isEmpty(localPath)) {
            LogUtil.d(TAG, "localPath is null");
            return null;
        }
        return MD5Util.md5(prefix + localPath + System.currentTimeMillis());
    }

    public static String getNsfwPhotoMd5(String localPath, String prefix) {
        if (TextUtils.isEmpty(localPath)) {
            LogUtil.d(TAG, "localPath is null");
            return null;
        }
        return MD5Util.md5(prefix + localPath);
    }
}