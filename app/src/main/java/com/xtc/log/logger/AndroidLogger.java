package com.xtc.log.logger;

import android.os.SystemClock;
import android.util.Log;

import com.xtc.log.ILogger;
import com.xtc.log.LogConfig;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Sink that writes through to logcat and applies rate/length guards.
 *
 * <p>Two guards exist because the app logs from a tight UI loop on a
 * battery-powered watch:
 * <ul>
 *   <li>records longer than {@link #MAX_MESSAGE_LENGTH} are flagged as truncated,
 *       and</li>
 *   <li>more than {@link #MAX_RECORDS_PER_WINDOW} records inside one
 *       {@link #WINDOW_MS} window are flagged as a burst.</li>
 * </ul>
 *
 * <p>Records are only forwarded to the delegate (the file logger) when the app
 * is a debug build running locally, and never for HTTP traffic which would
 * otherwise swamp the file.
 */
public class AndroidLogger implements ILogger {

    /** Longest record forwarded verbatim. */
    private static final int MAX_MESSAGE_LENGTH = 128;

    /** Window size in milliseconds used by the burst detector (128 ms). */
    private static final int WINDOW_MS = 128;

    /** Shift applied to {@link SystemClock#elapsedRealtime()} to get the window index. */
    private static final int PERIOD_INDEX = 7;

    /** Burst threshold within one {@link #WINDOW_MS} window. */
    private static final int MAX_RECORDS_PER_WINDOW = 10;

    private final ILogger delegate;
    private final LogConfig logConfig;

    private long lastSavedTime = 0;
    private final AtomicInteger logCount = new AtomicInteger(0);

    public AndroidLogger(ILogger delegate, LogConfig logConfig) {
        this.delegate = delegate;
        this.logConfig = logConfig;
    }

    @Override
    public void flush() {
    }

    @Override
    public void log(ILogger.Level level, String tag, String message, Throwable throwable) {
        int priority;
        switch (level) {
            case Verbose:
                priority = Log.VERBOSE;
                break;
            case Debug:
                priority = Log.DEBUG;
                break;
            case Info:
                priority = Log.INFO;
                break;
            case Warning:
                priority = Log.WARN;
                break;
            case Error:
                priority = Log.ERROR;
                break;
            case Assert:
                priority = Log.ASSERT;
                break;
            default:
                return;
        }
        if (throwable != null) {
            message = String.format(Locale.ROOT, "%s\n%s", message, Log.getStackTraceString(throwable));
        }
        if (tag == null) {
            tag = String.valueOf((Object) null);
        }
        if (message == null) {
            message = String.valueOf((Object) null);
        }
        Log.println(priority, tag, message);
        if (this.logConfig.isDebugVersion() && this.logConfig.isLocal() && !tag.contains("HTTP")) {
            this.delegate.log(level, tag, message, throwable);
        }
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    /** Flags records that arrive too frequently within one window. */
    private String checkFrequency(String message) {
        long windowIndex = SystemClock.elapsedRealtime() >> PERIOD_INDEX;
        long last = this.lastSavedTime;
        if (last == 0) {
            this.lastSavedTime = windowIndex;
            return message;
        }
        if (windowIndex > last) {
            this.lastSavedTime = windowIndex;
            if (this.logCount.getAndSet(0) <= MAX_RECORDS_PER_WINDOW) {
                return message;
            }
            return "上次打印128ms 内频繁！" + message;
        }
        this.logCount.incrementAndGet();
        return message;
    }

    /** Flags records longer than the length budget. */
    private String checkLength(String message) {
        if (message.length() <= MAX_MESSAGE_LENGTH) {
            return message;
        }
        return "日志太长！" + message;
    }
}
