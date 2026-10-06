package com.xtc.dataservice.api

import android.content.Context
import com.xtc.dataservice.api.domain.DataPoint
import com.xtc.dataservice.api.listener.IOnDataPointListener
import com.xtc.dataservice.api.listener.OnDataPointListener
import com.xtc.dataservice.api.request.FindLastKnownDataRequest
import com.xtc.dataservice.api.request.SensorRequest
import com.xtc.ipc.client.Client
import com.xtc.ipc.client.ServiceConfiguration
import com.xtc.ipc.client.Task

/** Client of the soft-sensor service. */
class SensorClient(context: Context) : Client<ISensorService>(
    ServiceConfiguration(context, NAME, ACTION),
    { binder -> ISensorService.Stub.asInterface(binder)!! }
) {

    fun registerSensorListener(request: SensorRequest, listener: OnDataPointListener): Task<Void> =
        registerListener(listener) { service ->
            service.registerSensorListener(request, OnDataPointListenerStub.obtain(listener))
        }

    fun unregisterSensorListener(listener: OnDataPointListener): Task<Void> =
        unregisterListener(listener) { service ->
            service.unregisterSensorListener(OnDataPointListenerStub.remove(listener))
        }

    fun findLastKnownData(request: FindLastKnownDataRequest): Task<List<DataPoint>> =
        execute { it.findLastKnownData(request) }

    /** Keeps a stable binder stub per listener so it can be re-registered after a reconnect. */
    private class OnDataPointListenerStub private constructor(
        private val listener: OnDataPointListener
    ) : IOnDataPointListener.Stub() {

        override fun onDataPoint(dataPoint: DataPoint) {
            listener.onDataPoint(dataPoint)
        }

        companion object {
            private val cache = HashMap<OnDataPointListener, OnDataPointListenerStub>()

            @JvmStatic
            @Synchronized
            fun obtain(listener: OnDataPointListener?): OnDataPointListenerStub? {
                if (listener == null) {
                    return null
                }
                return cache.getOrPut(listener) { OnDataPointListenerStub(listener) }
            }

            @JvmStatic
            @Synchronized
            fun remove(listener: OnDataPointListener?): OnDataPointListenerStub? = cache.remove(listener)
        }
    }

    private companion object {
        private const val NAME = "sensor_client"
        private const val ACTION = "com.xtc.dataservice.action.sensor_service"
    }
}