package com.xtc.ipc.client.internal

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection as AndroidServiceConnection
import android.os.IBinder
import android.os.RemoteException
import com.xtc.ipc.client.ServiceConfiguration
import com.xtc.ipc.client.findMatchedIntent
import com.xtc.log.LogUtil
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.LinkedList
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Single remote-service binding. Commands issued before the service is connected are
 * queued and replayed once the binder becomes available; listeners are re-registered
 * after a reconnect.
 */
internal class ServiceConnection(private val configuration: ServiceConfiguration) : CoroutineScope {

    private val name: String = configuration.name
    private val context: Context = configuration.context

    private val connection = object : AndroidServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            LogUtil.i(TAG, "$name: service connected")
            launch { onConnected(service) }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            LogUtil.i(TAG, "$name: service disconnected")
        }
    }

    private val binderDeathRecipient = IBinder.DeathRecipient {
        LogUtil.i(TAG, "$name: service died")
        launch { onRetryableDisconnected() }
    }

    private val pendingCommands = LinkedList<PendingCommand<*>>()
    private val keyToRegisterCommand = HashMap<Any, (IBinder) -> Unit>()

    private var binder: IBinder? = null
    private var reconnectJob: Job? = null

    override val coroutineContext: CoroutineContext
        get() = CoroutineName(name) + dispatcher

    /** Runs [block] on the bound service, connecting first when necessary. */
    suspend fun <R> execute(block: (IBinder) -> R): R = withContext(coroutineContext) {
        val current = binder
        if (current != null && current.isBinderAlive) {
            return@withContext block(current)
        }
        return@withContext suspendCancellableCoroutine<R> { continuation ->
            pendingCommands.add(PendingCommand(continuation, block))
            connect()
        }
    }

    /** Registers [block] so it is replayed after every reconnect. */
    suspend fun registerListener(key: Any?, block: (IBinder) -> Unit) {
        execute(block)
        if (key != null) {
            keyToRegisterCommand[key] = block
        }
    }

    /** Removes the listener registered under [key]. */
    suspend fun unregisterListener(key: Any?, block: (IBinder) -> Unit) {
        execute(block)
        if (key != null) {
            keyToRegisterCommand.remove(key)
        }
    }

    /** Unbinds the service. */
    suspend fun disconnect() = withContext(coroutineContext) {
        val current = binder
        if (current != null && current.isBinderAlive) {
            binder = null
            context.unbindService(connection)
        }
    }

    private fun connect() {
        if (!context.bindService(configuration.findMatchedIntent(), connection, Context.BIND_AUTO_CREATE)) {
            onRetryableDisconnected()
        }
    }

    private fun onConnected(service: IBinder?) {
        reconnectJob?.cancel()
        reconnectJob = null
        if (service == null) {
            LogUtil.w(TAG, "$name's binder == null")
            return
        }
        try {
            service.linkToDeath(binderDeathRecipient, 0)
            binder = service
            reRegisterListener(service)
            executePendingCommand { it.execute(service) }
        } catch (ignored: Throwable) {
            onRetryableDisconnected()
        }
    }

    private fun reRegisterListener(service: IBinder) {
        for ((key, block) in keyToRegisterCommand) {
            try {
                block(service)
            } catch (t: Throwable) {
                LogUtil.w(TAG, "reRegister $key's listener failure", t)
            }
        }
    }

    private fun executePendingCommand(action: (PendingCommand<*>) -> Unit) {
        while (pendingCommands.isNotEmpty()) {
            action(pendingCommands.pop())
        }
    }

    private fun onRetryableDisconnected() {
        binder = null
        executePendingCommand { command ->
            command.resumeWithException(RemoteException("$name: binder died"))
        }
        scheduleReconnect()
    }

    private fun scheduleReconnect() {
        if (reconnectJob != null) {
            return
        }
        reconnectJob = launch {
            for (retryCount in 1..MAX_RETRY_COUNT) {
                connect()
                delay(RETRY_INTERVAL_IN_MILLIS shl retryCount)
            }
        }
    }

    /** A command waiting for the binder, completed once the service is connected. */
    private class PendingCommand<T>(
        private val continuation: CancellableContinuation<T>,
        private val block: (IBinder) -> T
    ) {
        fun execute(binder: IBinder) {
            try {
                continuation.resume(block(binder))
            } catch (t: Throwable) {
                continuation.resumeWithException(t)
            }
        }

        fun resumeWithException(throwable: Throwable) {
            continuation.resumeWithException(throwable)
        }
    }

    private companion object {
        private const val TAG = "ServiceConnection"
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_INTERVAL_IN_MILLIS = 200L

        private val dispatcher = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, TAG)
        }.asCoroutineDispatcher()
    }
}