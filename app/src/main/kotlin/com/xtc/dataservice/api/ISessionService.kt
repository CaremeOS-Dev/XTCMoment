package com.xtc.dataservice.api

import android.os.Binder
import android.os.IBinder
import android.os.IInterface
import android.os.Parcel
import android.os.Parcelable
import android.os.RemoteException
import com.xtc.dataservice.api.domain.Session
import com.xtc.dataservice.api.listener.IOnDeadListener
import com.xtc.dataservice.api.listener.IOnSessionListener
import com.xtc.dataservice.api.request.FindSessionRequest
import com.xtc.dataservice.api.request.FindStartingSessionRequest
import com.xtc.dataservice.api.request.ReadSessionRequest
import com.xtc.dataservice.api.request.RegisterSessionListenerRequest

/** Remote session service. */
interface ISessionService : IInterface {

    @Throws(RemoteException::class)
    fun startSession(session: Session?): Session?

    @Throws(RemoteException::class)
    fun startSession(session: Session?, onDeadListener: IOnDeadListener?): Session?

    @Throws(RemoteException::class)
    fun findSession(sessionId: String?): Session?

    @Throws(RemoteException::class)
    fun findSession(request: FindSessionRequest?): Session?

    @Throws(RemoteException::class)
    fun findStartingSessions(request: FindStartingSessionRequest?): List<Session>

    @Throws(RemoteException::class)
    fun readSessions(request: ReadSessionRequest?): List<Session>

    @Throws(RemoteException::class)
    fun endSession(session: Session?): Session?

    @Throws(RemoteException::class)
    fun registerSessionListener(request: RegisterSessionListenerRequest?, listener: IOnSessionListener?)

    @Throws(RemoteException::class)
    fun unregisterSessionListener(listener: IOnSessionListener?)

    /** Server-side binder implementation. */
    abstract class Stub : Binder(), ISessionService {

        init {
            attachInterface(this, DESCRIPTOR)
        }

        override fun asBinder(): IBinder = this

        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            when (code) {
                TRANSACTION_START_SESSION -> {
                    data.enforceInterface(DESCRIPTOR)
                    writeSession(reply, startSession(readSession(data)))
                    return true
                }
                TRANSACTION_FIND_SESSION_BY_ID -> {
                    data.enforceInterface(DESCRIPTOR)
                    writeSession(reply, findSession(data.readString()))
                    return true
                }
                TRANSACTION_FIND_SESSION -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) FindSessionRequest.CREATOR.createFromParcel(data) else null
                    writeSession(reply, findSession(request))
                    return true
                }
                TRANSACTION_FIND_STARTING_SESSIONS -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) FindStartingSessionRequest.CREATOR.createFromParcel(data) else null
                    val result = findStartingSessions(request)
                    reply?.writeNoException()
                    reply?.writeTypedList(result)
                    return true
                }
                TRANSACTION_READ_SESSIONS -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) ReadSessionRequest.CREATOR.createFromParcel(data) else null
                    val result = readSessions(request)
                    reply?.writeNoException()
                    reply?.writeTypedList(result)
                    return true
                }
                TRANSACTION_REGISTER_LISTENER -> {
                    data.enforceInterface(DESCRIPTOR)
                    val request = if (data.readInt() != 0) RegisterSessionListenerRequest.CREATOR.createFromParcel(data) else null
                    registerSessionListener(request, IOnSessionListener.Stub.asInterface(data.readStrongBinder()))
                    reply?.writeNoException()
                    return true
                }
                TRANSACTION_UNREGISTER_LISTENER -> {
                    data.enforceInterface(DESCRIPTOR)
                    unregisterSessionListener(IOnSessionListener.Stub.asInterface(data.readStrongBinder()))
                    reply?.writeNoException()
                    return true
                }
                TRANSACTION_END_SESSION -> {
                    data.enforceInterface(DESCRIPTOR)
                    writeSession(reply, endSession(readSession(data)))
                    return true
                }
                TRANSACTION_START_SESSION_WITH_DEAD_LISTENER -> {
                    data.enforceInterface(DESCRIPTOR)
                    val session = readSession(data)
                    val onDeadListener = IOnDeadListener.Stub.asInterface(data.readStrongBinder())
                    writeSession(reply, startSession(session, onDeadListener))
                    return true
                }
                INTERFACE_TRANSACTION -> {
                    reply?.writeString(DESCRIPTOR)
                    return true
                }
                else -> return super.onTransact(code, data, reply, flags)
            }
        }

        private fun readSession(data: Parcel): Session? =
            if (data.readInt() != 0) Session.CREATOR.createFromParcel(data) else null

        private fun writeSession(reply: Parcel?, session: Session?) {
            if (reply == null) {
                return
            }
            reply.writeNoException()
            if (session != null) {
                reply.writeInt(1)
                session.writeToParcel(reply, 1)
            } else {
                reply.writeInt(0)
            }
        }

        private class Proxy(private val remote: IBinder) : ISessionService {

            override fun asBinder(): IBinder = remote

            override fun startSession(session: Session?): Session? = transactSession(TRANSACTION_START_SESSION, session)

            override fun startSession(session: Session?, onDeadListener: IOnDeadListener?): Session? {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeParcelable(data, session)
                    data.writeStrongBinder(onDeadListener?.asBinder())
                    remote.transact(TRANSACTION_START_SESSION_WITH_DEAD_LISTENER, data, reply, 0)
                    reply.readException()
                    return readSession(reply)
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun findSession(sessionId: String?): Session? {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    data.writeString(sessionId)
                    remote.transact(TRANSACTION_FIND_SESSION_BY_ID, data, reply, 0)
                    reply.readException()
                    return readSession(reply)
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun findSession(request: FindSessionRequest?): Session? {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeParcelable(data, request)
                    remote.transact(TRANSACTION_FIND_SESSION, data, reply, 0)
                    reply.readException()
                    return readSession(reply)
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            override fun findStartingSessions(request: FindStartingSessionRequest?): List<Session> =
                transactSessionList(TRANSACTION_FIND_STARTING_SESSIONS, request)

            override fun readSessions(request: ReadSessionRequest?): List<Session> =
                transactSessionList(TRANSACTION_READ_SESSIONS, request)

            override fun endSession(session: Session?): Session? = transactSession(TRANSACTION_END_SESSION, session)

            override fun registerSessionListener(
                request: RegisterSessionListenerRequest?,
                listener: IOnSessionListener?
            ) {
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

            override fun unregisterSessionListener(listener: IOnSessionListener?) {
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

            private fun transactSession(transaction: Int, session: Session?): Session? {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeParcelable(data, session)
                    remote.transact(transaction, data, reply, 0)
                    reply.readException()
                    return readSession(reply)
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            private fun transactSessionList(transaction: Int, request: Parcelable?): List<Session> {
                val data = Parcel.obtain()
                val reply = Parcel.obtain()
                try {
                    data.writeInterfaceToken(DESCRIPTOR)
                    writeParcelable(data, request)
                    remote.transact(transaction, data, reply, 0)
                    reply.readException()
                    return reply.createTypedArrayList(Session.CREATOR) ?: emptyList()
                } finally {
                    reply.recycle()
                    data.recycle()
                }
            }

            private fun readSession(reply: Parcel): Session? =
                if (reply.readInt() != 0) Session.CREATOR.createFromParcel(reply) else null

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
            private const val DESCRIPTOR = "com.xtc.dataservice.api.ISessionService"
            private const val TRANSACTION_START_SESSION = 1
            private const val TRANSACTION_FIND_SESSION_BY_ID = 2
            private const val TRANSACTION_FIND_SESSION = 3
            private const val TRANSACTION_FIND_STARTING_SESSIONS = 4
            private const val TRANSACTION_READ_SESSIONS = 5
            private const val TRANSACTION_REGISTER_LISTENER = 6
            private const val TRANSACTION_UNREGISTER_LISTENER = 7
            private const val TRANSACTION_END_SESSION = 8
            private const val TRANSACTION_START_SESSION_WITH_DEAD_LISTENER = 9

            @JvmStatic
            fun asInterface(binder: IBinder?): ISessionService? {
                if (binder == null) {
                    return null
                }
                val local = binder.queryLocalInterface(DESCRIPTOR)
                if (local is ISessionService) {
                    return local
                }
                return Proxy(binder)
            }
        }
    }
}