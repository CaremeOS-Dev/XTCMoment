package com.xtc.watch.bean

import com.google.gson.annotations.SerializedName

/**
 * Server payload describing the remote configuration.
 *
 * <p>[configVersion] may carry a {@code _}-separated suffix; only the trailing
 * segment is used for version comparisons (see [getVersion]).
 */
data class NetResponseConfig(
    @SerializedName("configVersion") var configVersion: String?,
    @SerializedName("expirationTime") var expirationTime: Long = 0L,
    @SerializedName("nextUploadTime") var nextUploadTime: Long = 0L,
    @SerializedName("configs") val configs: MutableList<Config>?,
    @SerializedName("appVersion") var appVersion: String? = null
) {

    /** Returns the config entry with the given key, or null. */
    fun includeKey(key: String): Config? {
        if (key.isEmpty()) {
            return null
        }
        return configs?.firstOrNull { it.key == key }
    }

    /** Schedules the next upload based on the expiration time. */
    fun updateNextUploadTime() {
        nextUploadTime = System.currentTimeMillis() + expirationMinutes() * 60L * 1000L
    }

    /** Version suffix used for comparisons. */
    fun getVersion(): String = configVersion?.split("_")?.lastOrNull() ?: ""

    private fun expirationMinutes(): Long = if (expirationTime <= 0) DEFAULT_EXPIRATION_TIME else expirationTime

    companion object {
        private const val DEFAULT_EXPIRATION_TIME = 1440L
    }
}