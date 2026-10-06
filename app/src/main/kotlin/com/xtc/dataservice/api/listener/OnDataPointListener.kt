package com.xtc.dataservice.api.listener

import com.xtc.dataservice.api.domain.DataPoint

/** Receives sensor data points. */
fun interface OnDataPointListener {
    fun onDataPoint(dataPoint: DataPoint)
}