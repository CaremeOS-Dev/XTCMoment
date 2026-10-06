package com.xtc.ipc.client

import android.os.IBinder
import android.os.RemoteException
import com.xtc.ipc.client.internal.ConnectionManager
import com.xtc.ipc.client.internal.DefaultTask
import com.xtc.ipc.client.internal.fromSuspend

/**
 * Base class of the remote-service clients. Subclasses describe how to build the
 * service interface from a binder; [Client] turns the suspend calls into [Task]s.
 */
abstract class Client<I : android.os.IInterface>(
    private val configuration: ServiceConfiguration,
    private val serviceGetter: (IBinder) -> I
) {

    /** A single remote call against the bound service interface. */
    fun interface RemoteOperator<I : android.os.IInterface, R> {
        @Throws(RemoteException::class)
        fun operate(binder: I): R
    }

    /** Executes [operator] and exposes its result as a [Task]. */
    protected fun <R> execute(operator: RemoteOperator<I, R>): Task<R> =
        fromSuspend { executeInternal(operator::operate) }

    /** Registers a listener with the remote service. */
    protected fun registerListener(key: Any?, operator: RemoteOperator<I, Unit>): Task<Void> =
        fromVoidTask { registerListenerInternal(key, operator::operate) }

    /** Unregisters a listener from the remote service. */
    protected fun unregisterListener(key: Any?, operator: RemoteOperator<I, Unit>): Task<Void> =
        fromVoidTask { unregisterListenerInternal(key, operator::operate) }

    /** Executes [operator] whose result is discarded, exposed as a `Task<Void>`. */
    @Suppress("UNCHECKED_CAST")
    protected fun executeVoid(operator: RemoteOperator<I, Unit>): Task<Void> =
        fromSuspend { executeInternal(operator::operate); null } as Task<Void>

    /** Releases the underlying service connection. */
    protected fun disconnect(): DefaultTask<Unit> = fromSuspend { disconnectInternal() }

    /**
     * Adapts a suspend action that produces no value into a `Task<Void>`, matching the
     * Java API where the success listener always receives {@code null}.
     */
    @Suppress("UNCHECKED_CAST")
    private fun fromVoidTask(block: suspend () -> Unit): Task<Void> =
        fromSuspend { block(); null } as Task<Void>

    private suspend fun <R> executeInternal(operator: (I) -> R): R =
        ConnectionManager.execute(configuration) { binder -> operator(serviceGetter(binder)) }

    private suspend fun registerListenerInternal(key: Any?, operator: (I) -> Unit) =
        ConnectionManager.registerListener(configuration, key) { binder -> operator(serviceGetter(binder)) }

    private suspend fun unregisterListenerInternal(key: Any?, operator: (I) -> Unit) =
        ConnectionManager.unregisterListener(configuration, key) { binder -> operator(serviceGetter(binder)) }

    private suspend fun disconnectInternal() = ConnectionManager.disconnect(configuration)
}