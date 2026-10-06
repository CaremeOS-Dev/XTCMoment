package com.xtc.dataservice.api

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.Parcelable
import android.os.RemoteException
import com.xtc.dataservice.api.domain.DataPoint
import com.xtc.dataservice.api.listener.IOnDataPointListener
import com.xtc.dataservice.api.request.FindLastKnownDataRequest
import com.xtc.dataservice.api.request.SensorRequest

/** Remote soft-sensor service. */
interface ISensorService : IInterface {

    @Throws(RemoteException::class)
    fun registerSensorListener(request: SensorRequest?, listener: IOnDataPointListener?)

    @Throws(RemoteException::class)
    fun unregisterSensorListener(listener: IOnDataPointListener?)

    @Throws(RemoteException::class)
    fun findLastKnownData(request: FindLastKnownDataRequest?): List<DataPoint>

    /** Server-side binder implementation. */
    abstract class Stub : Binder(), ISensorService {

        init {
            attachInterface(this, DESCRIPTOR)
        }

        override fun asBinder(): IBinder = this

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            when (code) {
                TRANSACTION_REGISTER_LISTENER -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) SensorRequest.CREATOR.createFromParcel(data) else null
                    registerSensorListener(request, IOnDataPointListener.Stub.asInterface(data.readStrongBinder()))
                    reply?.writeNoException()
                    return true
                }
                TRANSACTION_UNREGISTER_LISTENER -> {
                    data.enforceInterface(DESCRIPTOR)
                    unregisterSensorListener(IOnDataPointListener.Stub.asInterface(data.readStrongBinder()))
                    reply?.writeNoException()
                    return true
                }
                TRANSACTION_FIND_LAST_KNOWN_DATA -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) FindLastKnownDataRequest.CREATOR.createFromParcel(data) else null
                    val result = findLastKnownData(request)
                    reply?.writeNoException()
                    reply?.writeTypedList(result)
                    return true
                }
                INTERFACE_TRANSACTION -> {
                    reply?.writeString(DESCRIPTOR)
                    return true
                }
                else -> return super.onTransact(code, data, reply, flags)
            }
        }

        private class Proxy(private val remote: IBinder) : ISensorService {

            override fun asBinder(): IBinder = remote

            override fun registerSensorListener(request: SensorRequest?, listener: IOnDataPointListener?) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeParcelable(data, request)
                    data.writeStrongBinder(listener?.asBinder())
                    remote.transact(TRANSACTION_REGISTER_LISTENER, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun unregisterSensorListener(listener: IOnDataPointListener?) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeStrongBinder(listener?.asBinder())
                    remote.transact(TRANSACTION_UNREGISTER_LISTENER, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun findLastKnownData(request: FindLastKnownDataRequest?): List<DataPoint> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeParcelable(data, request)
                    remote.transact(TRANSACTION_FIND_LAST_KNOWN_DATA, data, reply, 0)
                    reply.readException()
                    return reply.createTypedArrayList(DataPoint.CREATOR) ?: emptyList()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            private fun writeParcelable(data: Parcel, value: Parcelable?) {
                if (value != null) {
                    data.writeInt(1)
                    value.writeToParcel(data, 0)
                } else {
                    data.writeInt(0)
                }
            }
        }

        companion object {
            private const val DESCRIPTOR = "com.xtc.dataservice.api.ISensorService"
            private const val TRANSACTION_REGISTER_LISTENER = 1
            private const val TRANSACTION_UNREGISTER_LISTENER = 2
            private const val TRANSACTION_FIND_LAST_KNOWN_DATA = 3

            @JvmStatic
            fun asInterface(binder: IBinder?): ISensorService? {
                if (binder == null) {
                    return null
                }
                val local = binder.queryLocalInterface(DESCRIPTOR)
                if (local is ISensorService) {
                    return local
                }
                return Proxy(binder)
            }
        }
    }
}