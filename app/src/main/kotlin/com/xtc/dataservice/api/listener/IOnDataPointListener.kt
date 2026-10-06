package com.xtc.dataservice.api.listener

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException
import com.xtc.dataservice.api.domain.DataPoint

/** Callback invoked for sensor data points. */
interface IOnDataPointListener : IInterface {

    @Throws(RemoteException::class)
    fun onDataPoint(dataPoint: DataPoint)

    /** Local-side binder implementation. */
    abstract class Stub : Binder(), IOnDataPointListener {

        init {
            attachInterface(this, DESCRIPTOR)
        }

        override fun asBinder(): IBinder = this

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code == TRANSACTION_ON_DATA_POINT) {
                data.enforceInterface(DESCRIPTOR)
                val dataPoint = if (data.readInt() != 0) DataPoint.CREATOR.createFromParcel(data) else null
                if (dataPoint != null) {
                    onDataPoint(dataPoint)
                }
                return true
            }
            if (code == INTERFACE_TRANSACTION) {
                reply?.writeString(DESCRIPTOR)
                return true
            }
            return super.onTransact(code, data, reply, flags)
        }

        private class Proxy(private val remote: IBinder) : IOnDataPointListener {

            override fun asBinder(): IBinder = remote

            override fun onDataPoint(dataPoint: DataPoint) {
                val data = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    if (dataPoint != null) {
                        data.writeInt(1)
                        dataPoint.writeToParcel(data, 0)
                    } else {
                        data.writeInt(0)
                    }
                    remote.transact(TRANSACTION_ON_DATA_POINT, data, null, 1)
                } finally {
                    data.recycle()
                }
            }
        }

        companion object {
            private const val DESCRIPTOR = "com.xtc.dataservice.api.listener.IOnDataPointListener"
            private const val TRANSACTION_ON_DATA_POINT = 1

            @JvmStatic
            fun asInterface(binder: IBinder?): IOnDataPointListener? {
                if (binder == null) {
                    return null
                }
                val local = binder.queryLocalInterface(DESCRIPTOR)
                if (local is IOnDataPointListener) {
                    return local
                }
                return Proxy(binder)
            }
        }
    }
}