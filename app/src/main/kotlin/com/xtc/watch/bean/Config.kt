package com.xtc.watch.bean

import com.google.gson.annotations.SerializedName

/** One configurable key with its conditions and default value. */
data class Config(
    @SerializedName("key") var key: String?,
    @SerializedName("description") var description: String?,
    @SerializedName("defaultValue") var defaultValue: String?,
    @SerializedName("conditions") var conditions: List<Condition>?,
    @SerializedName("type") var type: Int?
)