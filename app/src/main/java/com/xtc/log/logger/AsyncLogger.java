package com.xtc.log.logger;

import android.os.Process;

import com.xtc.log.ILogger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Moves log writes off the calling thread onto a single worker.
 *
 * <p>The queue is unbounded but the pool uses {@link ThreadPoolExecutor.DiscardPolicy},
 * so a burst of records never blocks the caller; it is simply dropped once the
 * worker cannot keep up. Records are stamped with the pid/tid they were emitted
 * from before being handed to the delegate.
 */
public class AsyncLogger implements ILogger {

    private final ILogger delegate;

    private final ThreadPoolExecutor logExecutor = new ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardPolicy());

    private final int pid = Process.myPid();

    public AsyncLogger(ILogger delegate) {
        this.delegate = delegate;
    }

    @Override
    public void log(ILogger.Level level, String tag, String message, Throwable throwable) {
        this.logExecutor.execute(new LogRunner(this.delegate, this.pid, Process.myTid(), level, tag, message, throwable));
    }

    @Override
    public void flush() {
        final CountDownLatch latch = new CountDownLatch(1);
        this.logExecutor.execute(new Runnable() {
            @Override
            public void run() {
                AsyncLogger.this.delegate.flush();
                latch.countDown();
            }
        });
        while (true) {
            try {
                latch.await();
                return;
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void close() {
        flush();
        this.delegate.close();
        this.logExecutor.shutdownNow();
    }

    /** Prepends the emitting pid/tid before the record reaches the delegate. */
    private static class LogRunner implements Runnable {

        private static final ThreadLocal<StringBuilder> STRING_BUILDER = new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                return new StringBuilder();
            }
        };

        private final ILogger delegate;
        private final int pid;
        private final long tid;
        private final ILogger.Level level;
        private final String tag;
        private final String message;
        private final Throwable throwable;

        public LogRunner(ILogger delegate, int pid, long tid, ILogger.Level level,
                         String tag, String message, Throwable throwable) {
            this.delegate = delegate;
            this.pid = pid;
            this.tid = tid;
            this.level = level;
            this.tag = tag;
            this.message = message;
            this.throwable = throwable;
        }

        @Override
        public void run() {
            if (this.delegate == null) {
                return;
            }
            StringBuilder builder = STRING_BUILDER.get();
            builder.setLength(0);
            builder.append('[');
            builder.append(this.pid);
            builder.append(':');
            builder.append(this.tid);
            builder.append(']');
            builder.append(this.tag);
            this.delegate.log(this.level, builder.toString(), this.message, this.throwable);
        }
    }
}
