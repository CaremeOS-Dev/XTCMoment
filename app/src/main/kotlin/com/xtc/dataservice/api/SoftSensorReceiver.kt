package com.xtc.dataservice.api

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.xtc.dataservice.api.domain.DataPoint

/** Receiver that delivers soft-sensor data points to the host app. */
abstract class SoftSensorReceiver : BroadcastReceiver() {

    abstract fun onDataPoint(dataPoint: DataPoint)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent == null || intent.action != ACTION_REGISTER_SENSOR) {
            return
        }
        val dataPoint = intent.getParcelableExtra<DataPoint>(EXTRA_DATA) ?: return
        onDataPoint(dataPoint)
    }

    companion object {
        const val ACTION_REGISTER_SENSOR = "com.xtc.dataservice.action.REGISTER_SENSOR"
        const val EXTRA_DATA = "data"
        const val EXTRA_REGISTER_SENSOR = "register_sensor"
    }
}