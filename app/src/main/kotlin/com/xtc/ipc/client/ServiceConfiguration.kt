package com.xtc.ipc.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo

/**
 * Identifies a remote service to bind to: the [context] that resolves it, a
 * human readable [name] used for logging and a unique [action].
 */
data class ServiceConfiguration(
    val context: Context,
    val name: String,
    val action: String
)

/** Service version metadata key published by the remote services. */
private const val SERVICE_VERSION_KEY = "com.xtc.service.version"

/**
 * Resolves the intent of the newest service implementation registered for the
 * configuration's action, falling back to the bare action when none is found.
 */
fun ServiceConfiguration.findMatchedIntent(): Intent {
    val result = context.packageManager.queryIntentServices(Intent(action), 128) ?: emptyList()
    val best = result.maxByOrNull { it.serviceVersion }
    return if (best == null) {
        Intent(action)
    } else {
        Intent(action).apply {
            component = ComponentName(best.serviceInfo.packageName, best.serviceInfo.name)
        }
    }
}

private val ResolveInfo.serviceVersion: Int
    get() = serviceInfo.metaData?.getInt(SERVICE_VERSION_KEY, 0) ?: 0