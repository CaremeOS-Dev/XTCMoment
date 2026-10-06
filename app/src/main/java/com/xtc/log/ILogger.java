package com.xtc.log;

/**
 * Sink for every log record the app emits.
 *
 * <p>The app funnels all logging through {@link Log}; swapping the sink at
 * startup (see {@link LogConfig.Builder.BuildMode}) switches logging for the
 * whole process without touching any call site.
 *
 * <p>Implementations must be thread safe: the app logs from background threads,
 * broadcast receivers and the UI at the same time.
 */
public interface ILogger {

    /** Severity of a log record. The ordinal is part of the stored contract. */
    enum Level {
        Verbose("V"),
        Debug("D"),
        Info("I"),
        Warning("W"),
        Error("E"),
        Assert("A"),
        None("");

        /** Single character used when the level is printed. */
        public final String consoleName;

        Level(String consoleName) {
            this.consoleName = consoleName;
        }

        /**
         * Maps a raw ordinal back to a level.
         *
         * @throws NoSuchLevelException when {@code index} is out of range; the
         *     stock code throws rather than returning null.
         */
        public static Level intToLevel(int index) throws NoSuchLevelException {
            try {
                return values()[index];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchLevelException(index);
            }
        }

        /** Raised when a stored level ordinal cannot be mapped back to a level. */
        public static class NoSuchLevelException extends Exception {
            NoSuchLevelException(int index) {
                super("没有这种级别的日志等级：" + index);
            }
        }
    }

    void log(Level level, String tag, String message, Throwable throwable);

    void flush();

    void close();
}
