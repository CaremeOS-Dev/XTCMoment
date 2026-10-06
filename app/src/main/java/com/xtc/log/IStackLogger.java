package com.xtc.log;

/**
 * A logger that additionally knows about the call site a record came from.
 *
 * <p>Used by {@code StandardOutLogger} when running off-device, where there is
 * no logcat to attach a stack trace to.
 */
public interface IStackLogger extends ILogger {

    void log(ILogger.Level level, StackInfo stackInfo, String tag, String message);

    /** Where a record was emitted from. */
    class StackInfo {
        public final String _fileName;
        public final String _className;
        public final String _methodName;
        public final int _lineNumber;
        public final int _pid;
        public final long _tid;
        public final long _mainTid;

        public StackInfo(String fileName, String className, String methodName,
                         int lineNumber, int pid, long tid, long mainTid) {
            this._fileName = fileName;
            this._className = className;
            this._methodName = methodName;
            this._lineNumber = lineNumber;
            this._pid = pid;
            this._tid = tid;
            this._mainTid = mainTid;
        }
    }

    /** A record together with its originating stack information. */
    class LogDatum {
        public final ILogger.Level _logLevel;
        public final StackInfo _stackInfo;
        public final String _tag;
        public final String _message;

        public LogDatum(ILogger.Level level, StackInfo stackInfo, String tag, String message) {
            this._logLevel = level;
            this._stackInfo = stackInfo;
            this._tag = tag;
            this._message = message;
        }
    }
}
