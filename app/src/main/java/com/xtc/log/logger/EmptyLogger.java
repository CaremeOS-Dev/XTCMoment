package com.xtc.log.logger;

import com.xtc.log.ILogger;

/** No-op sink installed until {@link com.xtc.log.LogConfig} builds the real stack. */
public enum EmptyLogger implements ILogger {
    Instance;

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void log(ILogger.Level level, String tag, String message, Throwable throwable) {
    }
}
