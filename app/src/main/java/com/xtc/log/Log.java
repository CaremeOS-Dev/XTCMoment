package com.xtc.log;

import com.xtc.log.logger.EmptyLogger;

/**
 * Process-wide log sink holder.
 *
 * <p>Every call site funnels through here, so installing a real sink during
 * application startup switches logging for the whole app. Until then records
 * are dropped by {@link EmptyLogger}.
 */
public class Log {

    /** Default sink: drops records until {@link LogConfig} installs a real one. */
    private static ILogger sLoggerImp = EmptyLogger.Instance;

    static void setLogger(ILogger logger) {
        sLoggerImp = logger;
    }

    public static ILogger getLogger() {
        return sLoggerImp;
    }

    public static void v(String tag, String message) {
        sLoggerImp.log(ILogger.Level.Verbose, tag, message, null);
    }

    public static void d(String tag, String message) {
        sLoggerImp.log(ILogger.Level.Debug, tag, message, null);
    }

    public static void i(String tag, String message) {
        sLoggerImp.log(ILogger.Level.Info, tag, message, null);
    }

    public static void w(String tag, String message) {
        sLoggerImp.log(ILogger.Level.Warning, tag, message, null);
    }

    public static void e(String tag, String message) {
        sLoggerImp.log(ILogger.Level.Error, tag, message, null);
    }

    public static void wtf(String tag, String message) {
        sLoggerImp.log(ILogger.Level.Assert, tag, message, null);
    }

    public static void v(String tag, String message, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Verbose, tag, message, throwable);
    }

    public static void d(String tag, String message, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Debug, tag, message, throwable);
    }

    public static void i(String tag, String message, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Info, tag, message, throwable);
    }

    public static void w(String tag, String message, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Warning, tag, message, throwable);
    }

    public static void e(String tag, String message, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Error, tag, message, throwable);
    }

    public static void wtf(String tag, String message, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Assert, tag, message, throwable);
    }

    /** Warning record carrying a throwable; logs with an empty message. */
    public static void w(String tag, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Warning, tag, "", throwable);
    }

    /** Assert record carrying a throwable; logs with an empty message. */
    public static void wtf(String tag, Throwable throwable) {
        sLoggerImp.log(ILogger.Level.Assert, tag, "", throwable);
    }

    public static void flush() {
        sLoggerImp.flush();
    }

    public static void close() {
        sLoggerImp.close();
    }
}
