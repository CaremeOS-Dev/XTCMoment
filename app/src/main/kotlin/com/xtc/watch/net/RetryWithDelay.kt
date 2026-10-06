package com.xtc.watch.net

import com.xtc.log.LogUtil
import rx.Observable
import rx.functions.Func1
import java.util.concurrent.TimeUnit

/**
 * Retries a failing observable with an exponential backoff.
 *
 * @param maxRetries number of retries before the error is re-emitted
 */
class RetryWithDelay @JvmOverloads constructor(private val maxRetries: Int = DEFAULT_MAX_RETRIES) :
    Func1<Observable<out Throwable>, Observable<*>> {

    private var retryCount = 0
    private var delaySeconds = INITIAL_DELAY_SECONDS

    override fun call(attempts: Observable<out Throwable>): Observable<*> =
        attempts.flatMap { throwable ->
            LogUtil.i(TAG, "call() throwable = $throwable  retryCount=$retryCount")
            retryCount++
            if (retryCount < maxRetries) {
                Observable.timer((delaySeconds shl retryCount).toLong(), TimeUnit.SECONDS)
            } else {
                Observable.error(throwable)
            }
        }

    private companion object {
        private const val TAG = "RetryWithDelay"
        private const val DEFAULT_MAX_RETRIES = 4
        private const val INITIAL_DELAY_SECONDS = 10
    }
}