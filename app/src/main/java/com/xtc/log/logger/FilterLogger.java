package com.xtc.log.logger;

import com.xtc.log.ILogger;
import com.xtc.log.LogConfig;

/**
 * Drops records below the configured level.
 *
 * <p>Records are only forwarded when the console mirror is on; debug builds
 * bypass the level check entirely.
 */
public class FilterLogger implements ILogger {

    private final ILogger logger;
    private final LogConfig logConfig;

    public FilterLogger(ILogger logger, LogConfig logConfig) {
        this.logger = logger;
        this.logConfig = logConfig;
    }

    @Override
    public void log(ILogger.Level level, String tag, String message, Throwable throwable) {
        LogConfig config = this.logConfig;
        if (config != null && config.isPrintConsole()) {
            int ordinal = level.ordinal();
            if (config.isDebugVersion() || ordinal >= config.getSaveLevel().ordinal()) {
                this.logger.log(level, tag, message, throwable);
            }
        }
    }

    @Override
    public void flush() {
        this.logger.flush();
    }

    @Override
    public void close() {
        this.logger.close();
    }
}
