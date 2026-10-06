package com.xtc.dataservice.api.listener

import com.xtc.dataservice.api.domain.DataPoint

/** Receives newly inserted data points. */
fun interface OnDataInsertListener {
    fun onDataPoint(dataPoint: DataPoint)
}