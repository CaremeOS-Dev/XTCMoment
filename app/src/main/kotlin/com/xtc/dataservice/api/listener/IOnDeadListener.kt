package com.xtc.dataservice.api.listener

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException

/** Marker callback invoked when the remote service dies. */
interface IOnDeadListener : IInterface {

    /** Local-side binder implementation. */
    abstract class Stub : Binder(), IOnDeadListener {

        init {
            attachInterface(this, DESCRIPTOR)
        }

        override fun asBinder(): IBinder = this

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            if (code == INTERFACE_TRANSACTION) {
                reply?.writeString(DESCRIPTOR)
                return true
            }
            return super.onTransact(code, data, reply, flags)
        }

        private class Proxy(private val remote: IBinder) : IOnDeadListener {
            override fun asBinder(): IBinder = remote
        }

        companion object {
            private const val DESCRIPTOR = "com.xtc.dataservice.api.listener.IOnDeadListener"

            @JvmStatic
            fun asInterface(binder: IBinder?): IOnDeadListener? {
                if (binder == null) {
                    return null
                }
                val local = binder.queryLocalInterface(DESCRIPTOR)
                if (local is IOnDeadListener) {
                    return local
                }
                return Proxy(binder)
            }
        }
    }
}