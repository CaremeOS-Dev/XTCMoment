package com.xtc.watch

import android.content.Context
import com.xtc.utils.storage.SharedManager

/**
 * Persists the timestamp of the last successful config update.
 */
class SpManager(context: Context) {

    private val manager: SharedManager by lazy { SharedManager.getInstance(context) }

    /** Saves the update timestamp. */
    fun saveUpdateTime(time: Long) {
        manager.putLong(UPDATE_TIME, time)
    }

    /** Reads the update timestamp, defaulting to 0. */
    fun getUpdateTime(): Long = manager.getLong(UPDATE_TIME, 0L)

    companion object {
        private const val UPDATE_TIME = "update_time"
    }
}