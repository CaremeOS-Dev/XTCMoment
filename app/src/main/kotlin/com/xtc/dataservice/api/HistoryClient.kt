package com.xtc.dataservice.api

import android.content.Context
import com.xtc.dataservice.api.domain.DataPoint
import com.xtc.dataservice.api.listener.IOnDataInsertListener
import com.xtc.dataservice.api.listener.OnDataInsertListener
import com.xtc.dataservice.api.request.DeleteDataRequest
import com.xtc.dataservice.api.request.FindLastKnownDataRequest
import com.xtc.dataservice.api.request.ReadDataRequest
import com.xtc.dataservice.api.request.RegisterDataInsertListenerRequest
import com.xtc.ipc.client.Client
import com.xtc.ipc.client.ServiceConfiguration
import com.xtc.ipc.client.Task

/** Client of the history (data-point) service. */
class HistoryClient(context: Context) : Client<IHistoryService>(
    ServiceConfiguration(context, NAME, ACTION),
    { binder -> IHistoryService.Stub.asInterface(binder)!! }
) {

    fun findLastKnownData(request: FindLastKnownDataRequest): Task<List<DataPoint>> =
        execute { it.findLastKnownData(request) }

    fun readData(request: ReadDataRequest): Task<List<DataPoint>> = execute { it.readData(request) }

    fun deleteData(request: DeleteDataRequest): Task<Void> = executeVoid { it.deleteData(request) }

    fun registerDataInsertListener(
        request: RegisterDataInsertListenerRequest,
        listener: OnDataInsertListener
    ): Task<Void> = registerListener(listener) { service ->
        service.registerDataInsertListener(request, OnDataInsertListenerStub.obtain(listener))
    }

    fun unregisterDataInsertListener(listener: OnDataInsertListener): Task<Void> =
        unregisterListener(listener) { service ->
            service.unregisterDataInsertListener(OnDataInsertListenerStub.remove(listener))
        }

    /** Keeps a stable binder stub per listener so it can be re-registered after a reconnect. */
    private class OnDataInsertListenerStub private constructor(
        private val listener: OnDataInsertListener
    ) : IOnDataInsertListener.Stub() {

        override fun onDataPoint(dataPoint: DataPoint) {
            listener.onDataPoint(dataPoint)
        }

        companion object {
            private val cache = HashMap<OnDataInsertListener, OnDataInsertListenerStub>()

            @JvmStatic
            @Synchronized
            fun obtain(listener: OnDataInsertListener?): OnDataInsertListenerStub? {
                if (listener == null) {
                    return null
                }
                return cache.getOrPut(listener) { OnDataInsertListenerStub(listener) }
            }

            @JvmStatic
            @Synchronized
            fun remove(listener: OnDataInsertListener?): OnDataInsertListenerStub? = cache.remove(listener)
        }
    }

    private companion object {
        private const val NAME = "history_client"
        private const val ACTION = "com.xtc.dataservice.action.history_service"
    }
}