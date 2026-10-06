package com.xtc.watch.bean

import com.xtc.watch.ConfigConstant

/** Request body for an incremental config update. */
data class NetUpdateParam(
    var configVersion: String,
    val field: String = ConfigConstant.Field.ANDROID_WATCH,
    val model: String = "",
    val appPackage: String = "",
    val appVersion: String = ""
)