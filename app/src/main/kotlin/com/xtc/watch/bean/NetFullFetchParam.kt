package com.xtc.watch.bean

import com.google.gson.annotations.SerializedName

/** Request body for a full config fetch. */
data class NetFullFetchParam(
    @SerializedName("field") val field: String?,
    @SerializedName("model") val model: String?
)