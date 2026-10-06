package com.xtc.dataservice.api.listener

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.RemoteException
import com.xtc.dataservice.api.domain.Session

/** Callback invoked when a session starts or ends. */
interface IOnSessionListener : IInterface {

    @Throws(RemoteException::class)
    fun onSessionStart(session: Session)

    @Throws(RemoteException::class)
    fun onSessionEnd(session: Session)

    /** Local-side binder implementation. */
    abstract class Stub : Binder(), IOnSessionListener {

        init {
            attachInterface(this, DESCRIPTOR)
        }

        override fun asBinder(): IBinder = this

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            when (code) {
                TRANSACTION_ON_SESSION_START -> {
                    data.enforceInterface(DESCRIPTOR)
                    val session = if (data.readInt() != 0) Session.CREATOR.createFromParcel(data) else null
                    if (session != null) {
                        onSessionStart(session)
                    }
                    return true
                }
                TRANSACTION_ON_SESSION_END -> {
                    data.enforceInterface(DESCRIPTOR)
                    val session = if (data.readInt() != 0) Session.CREATOR.createFromParcel(data) else null
                    if (session != null) {
                        onSessionEnd(session)
                    }
                    return true
                }
                INTERFACE_TRANSACTION -> {
                    reply?.writeString(DESCRIPTOR)
                    return true
                }
                else -> return super.onTransact(code, data, reply, flags)
            }
        }

        private class Proxy(private val remote: IBinder) : IOnSessionListener {

            override fun asBinder(): IBinder = remote

            override fun onSessionStart(session: Session) = writeSession(TRANSACTION_ON_SESSION_START, session)

            override fun onSessionEnd(session: Session) = writeSession(TRANSACTION_ON_SESSION_END, session)

            private fun writeSession(transaction: Int, session: Session) {
                val data = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    if (session != null) {
                        data.writeInt(1)
                        session.writeToParcel(data, 0)
                    } else {
                        data.writeInt(0)
                    }
                    remote.transact(transaction, data, null, 1)
                } finally {
                    data.recycle()
                }
            }
        }

        companion object {
            private const val DESCRIPTOR = "com.xtc.dataservice.api.listener.IOnSessionListener"
            private const val TRANSACTION_ON_SESSION_START = 1
            private const val TRANSACTION_ON_SESSION_END = 2

            @JvmStatic
            fun asInterface(binder: IBinder?): IOnSessionListener? {
                if (binder == null) {
                    return null
                }
                val local = binder.queryLocalInterface(DESCRIPTOR)
                if (local is IOnSessionListener) {
                    return local
                }
                return Proxy(binder)
            }
        }
    }
}