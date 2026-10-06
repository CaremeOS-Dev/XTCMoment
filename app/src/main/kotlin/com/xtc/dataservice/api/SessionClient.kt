package com.xtc.dataservice.api

import android.content.Context
import com.xtc.dataservice.api.domain.Session
import com.xtc.dataservice.api.listener.IOnDeadListener
import com.xtc.dataservice.api.listener.IOnSessionListener
import com.xtc.dataservice.api.listener.OnSessionListener
import com.xtc.dataservice.api.request.FindSessionRequest
import com.xtc.dataservice.api.request.FindStartingSessionRequest
import com.xtc.dataservice.api.request.ReadSessionRequest
import com.xtc.dataservice.api.request.RegisterSessionListenerRequest
import com.xtc.ipc.client.Client
import com.xtc.ipc.client.ServiceConfiguration
import com.xtc.ipc.client.Task

/** Client of the session service. */
class SessionClient(context: Context) : Client<ISessionService>(
    ServiceConfiguration(context, NAME, ACTION),
    { binder -> ISessionService.Stub.asInterface(binder)!! }
) {

    /** Starts [session] and notifies [onDeadListener] when the session ends. */
    fun startSession(session: Session, onDeadListener: IOnDeadListener): Task<Session?> =
        execute { it.startSession(session, onDeadListener) }

    /** Ends [session]. */
    fun endSession(session: Session): Task<Session?> = execute { it.endSession(session) }

    fun findSession(sessionId: String): Task<Session?> = execute { it.findSession(sessionId) }

    fun findSession(request: FindSessionRequest): Task<Session?> = execute { it.findSession(request) }

    fun findStartingSessions(request: FindStartingSessionRequest): Task<List<Session>> =
        execute { it.findStartingSessions(request) }

    fun readSessions(request: ReadSessionRequest): Task<List<Session>> = execute { it.readSessions(request) }

    fun registerSessionListener(
        request: RegisterSessionListenerRequest,
        listener: OnSessionListener
    ): Task<Void> = registerListener(listener) { service ->
        service.registerSessionListener(request, OnSessionListenerStub.obtain(listener))
    }

    fun unregisterSessionListener(listener: OnSessionListener): Task<Void> =
        unregisterListener(listener) { service ->
            service.unregisterSessionListener(OnSessionListenerStub.remove(listener))
        }

    /** Keeps a stable binder stub per listener so it can be re-registered after a reconnect. */
    private class OnSessionListenerStub private constructor(
        private val listener: OnSessionListener
    ) : IOnSessionListener.Stub() {

        override fun onSessionStart(session: Session) {
            listener.onSessionStart(session)
        }

        override fun onSessionEnd(session: Session) {
            listener.onSessionEnd(session)
        }

        companion object {
            private val cache = HashMap<OnSessionListener, OnSessionListenerStub>()

            @JvmStatic
            @Synchronized
            fun obtain(listener: OnSessionListener?): OnSessionListenerStub? {
                if (listener == null) {
                    return null
                }
                return cache.getOrPut(listener) { OnSessionListenerStub(listener) }
            }

            @JvmStatic
            @Synchronized
            fun remove(listener: OnSessionListener?): OnSessionListenerStub? = cache.remove(listener)
        }
    }

    private companion object {
        private const val NAME = "session_client"
        private const val ACTION = "com.xtc.dataservice.action.session_service"
    }
}