package com.xtc.log.logger;

import com.xtc.log.ILogger;
import com.xtc.log.IStackLogger;
import com.xtc.log.util.StackUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Sink that prints to standard out, used when the app runs as plain Java.
 *
 * <p>Each line is prefixed with a timestamp, the thread id and the level, which
 * makes the off-device output resemble the logcat format.
 */
public enum StandardOutLogger implements IStackLogger {
    Instance;

    /** How far up the stack the call site is, from inside {@link #log}. */
    private static final int StackPosition = 6;

    /** Separator placed between the level and the tag. */
    private static final String SEPARATOR = "/";

    @Override
    public void log(ILogger.Level level, IStackLogger.StackInfo stackInfo, String tag, String message) {
        System.out.println(generateLogHeader(level) + tag + ": " + StackUtils.generateConsoleMessage(stackInfo, message));
    }

    @Override
    public void log(ILogger.Level level, String tag, String message, Throwable throwable) {
        log(level, StackUtils.makeStackInfo(StackPosition, 0, 0L), tag, message);
    }

    @Override
    public void flush() {
        System.out.flush();
    }

    @Override
    public void close() {
        flush();
    }

    public String generateLogHeader(ILogger.Level level) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        return new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINA).format(calendar.getTime())
                + " " + Thread.currentThread().getId() + " " + level.consoleName + SEPARATOR;
    }
}
