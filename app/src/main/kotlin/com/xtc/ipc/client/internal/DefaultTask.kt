package com.xtc.ipc.client.internal

import com.xtc.ipc.client.Task
import java.util.concurrent.Executor

/**
 * Default [Task] implementation. Completion state is latched, so listeners
 * attached later are invoked with the already-known result.
 */
class DefaultTask<V> : Task<V> {

    private var successListeners: MutableList<ListenerHolder<Task.OnSuccessListener<V>>>? = null
    private var failureListeners: MutableList<ListenerHolder<Task.OnFailureListener>>? = null
    private var result: Any? = null

    private fun isComplete(): Boolean = result != null

    private fun isSuccess(): Boolean = result !is FailureHolder

    @Suppress("UNCHECKED_CAST")
    private fun successValue(): V? = if (result !== NONE) result as V? else null

    private fun failure(): Throwable = (result as FailureHolder).throwable

    /** Completes the task successfully. */
    fun setResult(value: V?) {
        complete(value ?: NONE)
    }

    /** Completes the task with a failure. */
    fun setFailure(throwable: Throwable?) {
        if (throwable == null) {
            throw NullPointerException("failure == null")
        }
        complete(FailureHolder(throwable))
    }

    @Synchronized
    private fun complete(value: Any?) {
        if (isComplete()) {
            return
        }
        result = value
        if (isSuccess()) {
            val current = successValue()
            successListeners?.forEach { holder ->
                holder.executor.execute { holder.listener.onSuccess(current as V) }
            }
        } else {
            val throwable = failure()
            failureListeners?.forEach { holder ->
                holder.executor.execute { holder.listener.onFailure(throwable) }
            }
        }
        successListeners = null
        failureListeners = null
    }

    override fun onSuccess(listener: Task.OnSuccessListener<V>): Task<V> =
        onSuccess(DIRECT_EXECUTOR, listener)

    override fun onFailure(listener: Task.OnFailureListener): Task<V> =
        onFailure(DIRECT_EXECUTOR, listener)

    @Synchronized
    override fun onSuccess(executor: Executor, listener: Task.OnSuccessListener<V>): Task<V> {
        requireNotNull(executor) { "executor == null" }
        requireNotNull(listener) { "listener == null" }
        if (!isComplete()) {
            val list = successListeners ?: ArrayList<ListenerHolder<Task.OnSuccessListener<V>>>(2).also { successListeners = it }
            list.add(ListenerHolder(executor, listener))
            return this
        }
        if (isSuccess()) {
            val current = successValue()
            executor.execute { listener.onSuccess(current as V) }
        }
        return this
    }

    @Synchronized
    override fun onFailure(executor: Executor, listener: Task.OnFailureListener): Task<V> {
        requireNotNull(executor) { "executor == null" }
        requireNotNull(listener) { "listener == null" }
        if (!isComplete()) {
            val list = failureListeners ?: ArrayList<ListenerHolder<Task.OnFailureListener>>(2).also { failureListeners = it }
            list.add(ListenerHolder(executor, listener))
            return this
        }
        if (!isSuccess()) {
            val throwable = failure()
            executor.execute { listener.onFailure(throwable) }
        }
        return this
    }

    private class FailureHolder(val throwable: Throwable)

    private class ListenerHolder<L>(val executor: Executor, val listener: L)

    private companion object {
        private val DIRECT_EXECUTOR = Executor { it.run() }
        private val NONE = Any()
    }
}