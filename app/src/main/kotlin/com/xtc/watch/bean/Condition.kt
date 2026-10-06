package com.xtc.watch.bean

import com.google.gson.annotations.SerializedName

/**
 * Matching rule for a [Config] entry: the entry applies when every populated
 * field matches the current environment.
 */
data class Condition(
    @SerializedName("models") var models: List<String>?,
    @SerializedName("value") var value: String?,
    @SerializedName("appPackage") var appPackage: String?,
    @SerializedName("appVersion") var appVersion: String?,
    @SerializedName("language") var language: String?,
    @SerializedName("color") var color: String?
)