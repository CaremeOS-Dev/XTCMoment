package com.xtc.qiniu;

import android.content.Context;

import com.xtc.utils.storage.SharedManager;

/** Persists the per-space upload token. */
public class SharedTool {

    private SharedTool() {
    }

    public static String getUploadToken(Context context, int spaceType) {
        return SharedManager.getInstance(context).getString(Constants.ICloud.UPLOAD_TOKEN + spaceType, "");
    }

    public static void saveUploadToken(Context context, int spaceType, String token) {
        SharedManager.getInstance(context).putString(Constants.ICloud.UPLOAD_TOKEN + spaceType, token);
    }
}