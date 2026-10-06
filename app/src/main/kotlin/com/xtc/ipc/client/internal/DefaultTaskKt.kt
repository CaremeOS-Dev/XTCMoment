package com.xtc.ipc.client.internal

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.Continuation

/** Dedicated scope for the coroutines backing the [DefaultTask]s. */
private val taskCoroutineScope: CoroutineScope = CoroutineScope(CoroutineName("DefaultTask") + Dispatchers.Default)

/**
 * Runs [block] on [taskCoroutineScope] and exposes its result as a [DefaultTask].
 */
fun <R> fromSuspend(block: suspend () -> R): DefaultTask<R> {
    val task = DefaultTask<R>()
    taskCoroutineScope.launch {
        try {
            task.setResult(block())
        } catch (t: Throwable) {
            task.setFailure(t)
        }
    }
    return task
}