package com.xtc.ipc.client.internal

import android.os.IBinder
import com.xtc.ipc.client.ServiceConfiguration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.LinkedHashMap

/**
 * Keeps one [ServiceConnection] per [ServiceConfiguration] and serializes the
 * creation of new connections.
 */
internal object ConnectionManager {

    private val mutex = Mutex()
    private val configurationToConnection = LinkedHashMap<ServiceConfiguration, ServiceConnection>()

    suspend fun <R> execute(configuration: ServiceConfiguration, block: (IBinder) -> R): R =
        getOrCreateConnection(configuration).execute(block)

    suspend fun registerListener(configuration: ServiceConfiguration, key: Any?, block: (IBinder) -> Unit) =
        getOrCreateConnection(configuration).registerListener(key, block)

    suspend fun unregisterListener(configuration: ServiceConfiguration, key: Any?, block: (IBinder) -> Unit) =
        getOrCreateConnection(configuration).unregisterListener(key, block)

    suspend fun disconnect(configuration: ServiceConfiguration) =
        getOrCreateConnection(configuration).disconnect()

    private suspend fun getOrCreateConnection(configuration: ServiceConfiguration): ServiceConnection =
        mutex.withLock {
            configurationToConnection.getOrPut(configuration) { ServiceConnection(configuration) }
        }
}