package com.xtc.dataservice.api

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException
import com.xtc.dataservice.api.domain.DataPoint
import com.xtc.dataservice.api.listener.IOnDataInsertListener
import com.xtc.dataservice.api.request.DeleteDataRequest
import com.xtc.dataservice.api.request.FindLastKnownDataRequest
import com.xtc.dataservice.api.request.ReadDataRequest
import com.xtc.dataservice.api.request.RegisterDataInsertListenerRequest

/** Remote history (data-point) service. */
interface IHistoryService : IInterface {

    @Throws(RemoteException::class)
    fun findLastKnownData(request: FindLastKnownDataRequest?): List<DataPoint>

    @Throws(RemoteException::class)
    fun readData(request: ReadDataRequest?): List<DataPoint>

    @Throws(RemoteException::class)
    fun deleteData(request: DeleteDataRequest?)

    @Throws(RemoteException::class)
    fun registerDataInsertListener(request: RegisterDataInsertListenerRequest?, listener: IOnDataInsertListener?)

    @Throws(RemoteException::class)
    fun unregisterDataInsertListener(listener: IOnDataInsertListener?)

    /** Server-side binder implementation. */
    abstract class Stub : Binder(), IHistoryService {

        init {
            attachInterface(this, DESCRIPTOR)
        }

        override fun asBinder(): IBinder = this

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            when (code) {
                TRANSACTION_FIND_LAST_KNOWN_DATA -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) FindLastKnownDataRequest.CREATOR.createFromParcel(data) else null
                    val result = findLastKnownData(request)
                    reply?.writeNoException()
                    reply?.writeTypedList(result)
                    return true
                }
                TRANSACTION_READ_DATA -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) ReadDataRequest.CREATOR.createFromParcel(data) else null
                    val result = readData(request)
                    reply?.writeNoException()
                    reply?.writeTypedList(result)
                    return true
                }
                TRANSACTION_DELETE_DATA -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) DeleteDataRequest.CREATOR.createFromParcel(data) else null
                    deleteData(request)
                    reply?.writeNoException()
                    return true
                }
                TRANSACTION_REGISTER_LISTENER -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) RegisterDataInsertListenerRequest.CREATOR.createFromParcel(data) else null
                    registerDataInsertListener(request, IOnDataInsertListener.Stub.asInterface(data.readStrongBinder()))
                    reply?.writeNoException()
                    return true
                }
                TRANSACTION_UNREGISTER_LISTENER -> {
                    data.enforceInterface(DESCRIPTOR)
                    unregisterDataInsertListener(IOnDataInsertListener.Stub.asInterface(data.readStrongBinder()))
                    reply?.writeNoException()
                    return true
                }
                INTERFACE_TRANSACTION -> {
                    reply?.writeString(DESCRIPTOR)
                    return true
                }
                else -> return super.onTransact(code, data, reply, flags)
            }
        }

        private class Proxy(private val remote: IBinder) : IHistoryService {

            override fun asBinder(): IBinder = remote

            override fun findLastKnownData(request: FindLastKnownDataRequest?): List<DataPoint> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeRequest(data, request)
                    remote.transact(TRANSACTION_FIND_LAST_KNOWN_DATA, data, reply, 0)
                    reply.readException()
                    return reply.createTypedArrayList(DataPoint.CREATOR) ?: emptyList()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun readData(request: ReadDataRequest?): List<DataPoint> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeRequest(data, request)
                    remote.transact(TRANSACTION_READ_DATA, data, reply, 0)
                    reply.readException()
                    return reply.createTypedArrayList(DataPoint.CREATOR) ?: emptyList()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun deleteData(request: DeleteDataRequest?) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeRequest(data, request)
                    remote.transact(TRANSACTION_DELETE_DATA, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun registerDataInsertListener(
                request: RegisterDataInsertListenerRequest?,
                listener: IOnDataInsertListener?
            ) {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeRequest(data, request)
                    data.writeStrongBinder(listener?.asBinder())
                    remote.transact(TRANSACTION_REGISTER_LISTENER, data, reply, 0)
                    reply.readException()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun unregisterDataInsertListener(listener: IOnDataInsertListener?) {
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

            private fun writeRequest(data: Parcel, request: android.os.Parcelable?) {
                if (request != null) {
                    data.writeInt(1)
                    request.writeToParcel(data, 0)
                } else {
                    data.writeInt(0)
                }
            }
        }

        companion object {
            private const val DESCRIPTOR = "com.xtc.dataservice.api.IHistoryService"
            private const val TRANSACTION_FIND_LAST_KNOWN_DATA = 1
            private const val TRANSACTION_READ_DATA = 2
            private const val TRANSACTION_DELETE_DATA = 3
            private const val TRANSACTION_REGISTER_LISTENER = 4
            private const val TRANSACTION_UNREGISTER_LISTENER = 5

            @JvmStatic
            fun asInterface(binder: IBinder?): IHistoryService? {
                if (binder == null) {
                    return null
                }
                val local = binder.queryLocalInterface(DESCRIPTOR)
                if (local is IHistoryService) {
                    return local
                }
                return Proxy(binder)
            }
        }
    }
}