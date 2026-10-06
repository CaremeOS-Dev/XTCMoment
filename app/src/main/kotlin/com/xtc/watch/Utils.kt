package com.xtc.watch

import android.content.Context
import com.xtc.log.LogUtil

/** Small helpers for the watch config library. */
object Utils {

    /** Returns the version code of the host app as a string, or an empty string. */
    @JvmStatic
    fun getAppVersion(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionCode.toString()
        } catch (e: Exception) {
            LogUtil.i("Utils", "getAppVersion() context = $context")
            ""
        }
    }
}