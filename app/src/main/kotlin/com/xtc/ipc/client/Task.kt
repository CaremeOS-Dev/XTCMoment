package com.xtc.ipc.client

import java.util.concurrent.Executor

/**
 * Handle of an asynchronous remote call. Listeners attached after completion
 * are still invoked, mirroring the behaviour of the original implementation.
 */
interface Task<V> {

    fun interface OnFailureListener {
        fun onFailure(throwable: Throwable)
    }

    fun interface OnSuccessListener<V> {
        fun onSuccess(value: V)
    }

    fun onFailure(listener: OnFailureListener): Task<V>

    fun onSuccess(listener: OnSuccessListener<V>): Task<V>

    fun onFailure(executor: Executor, listener: OnFailureListener): Task<V>

    fun onSuccess(executor: Executor, listener: OnSuccessListener<V>): Task<V>
}