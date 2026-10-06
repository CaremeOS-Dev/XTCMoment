package com.xtc.watch.bean

/** Request body for the "get config by keys" endpoint. */
data class NetConfigParam(
    val field: String,
    val keys: List<String>,
    val model: String?,
    val appPackage: String?,
    val appVersion: String?,
    val language: String?,
    val color: String?
)